/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.tools.api.preferences.SiriusDiagramPreferencesKeys;
import org.eclipse.sirius.diagram.ui.internal.edit.parts.DDiagramEditPart;
import org.eclipse.sirius.diagram.ui.tools.api.editor.DDiagramEditor;
import org.eclipse.sirius.diagram.ui.tools.api.requests.RequestConstants;
import org.eclipse.sirius.diagram.ui.tools.internal.layout.LayoutUtil;
import org.eclipse.sirius.ecore.extender.business.api.permission.exception.LockedInstanceException;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.ui.business.api.preferences.SiriusUIPreferencesKeys;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.DRepresentationDescriptor;
import org.eclipse.sirius.viewpoint.SiriusPlugin;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.eclipse.sirius.viewpoint.provider.SiriusEditPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.polarsys.capella.core.sirius.analysis.task.CreateViewTask;

import com.navalgroup.capella.cross.analysis.design.Messages;
import com.navalgroup.capella.permission.util.PermissionRegistryUtil;
import com.navalgroup.capella.permission.util.PermissionUtil;

/**
 * Services using Sirius.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
@SuppressWarnings("restriction")
public final class SiriusUtil {

	/**
	 * Constructor.
	 */
	private SiriusUtil() {
	}

	/**
	 * Create views for capellaElements in diagram.
	 * 
	 * @param session
	 *            Session
	 * @param diagram
	 *            DSemanticDiagram
	 * @param elements
	 *            List<EObject>
	 * @param monitor
	 *            IProgressMonitor
	 */
	public static void createViews(Session session, DSemanticDiagram diagram, List<? extends EObject> elements,
			IProgressMonitor monitor) {
		monitor.beginTask(Messages.CrossAnalysisRunnable_CreateViewTaskLabel, elements.size());
		List<CreateViewTask> tasks = new ArrayList<>();
		elements.forEach(el -> {
			tasks.add(createView(session, diagram, el));
			monitor.worked(1);
		});
		session.getTransactionalEditingDomain().getCommandStack().execute(
				new RecordingCommand(session.getTransactionalEditingDomain(), Messages.SiriusUtil_CreateViewCommand) {

					@Override
					protected void doExecute() {
						tasks.forEach(t -> t.execute());
					}
				});
		monitor.worked(1);
	}

	/**
	 * Create a view for context in Cross Layers Diagram.
	 * 
	 * @param session
	 *            Session
	 * @param diagram
	 *            DSemanticDiagram
	 * @param context
	 *            ModelElement
	 */
	public static CreateViewTask createView(Session session, DSemanticDiagram diagram, EObject context) {
		return new CreateViewTask(context, diagram, context, null, "view") {

			/**
			 * By default return true, but InitCLD launch arrange all -> turn to false by
			 * default.
			 */
			protected boolean autoPinOnCreateEnabled() {
				return Platform.getPreferencesService().getBoolean(SiriusPlugin.ID,
						SiriusDiagramPreferencesKeys.PREF_AUTO_PIN_ON_CREATE.name(), false, new IScopeContext[0]);
			}
		};
	}

	/**
	 * Create a representation.
	 * 
	 * @param element
	 *            EObject
	 * @param session
	 *            Session
	 * @param representationDescription
	 *            RepresentationDescription
	 * @param representationName
	 *            String
	 * @param monitor
	 *            IProgressMonitor
	 * @return created representation
	 */
	public static Optional<DSemanticDiagram> createRepresentation(EObject element, Session session,
			RepresentationDescription representationDescription, String representationName, IProgressMonitor monitor) {
		// run diagram creation in UI Thread
		CreateDRepresentationRunnable createDRepresentationRunnable = new CreateDRepresentationRunnable(element,
				session, representationDescription, representationName, monitor);
		Display.getDefault().syncExec(createDRepresentationRunnable);

		// get created diagram
		if (createDRepresentationRunnable.getStatus().isOK()) {
			DRepresentation createdDRepresentation = createDRepresentationRunnable.getResult();
			// cast representation to DSemanticDiagram
			if (createdDRepresentation instanceof DSemanticDiagram) {
				return Optional.ofNullable((DSemanticDiagram) createdDRepresentation);
			}
		} else {
			if (createDRepresentationRunnable.getStatus().matches(Status.CANCEL)) {
				throw new OperationCanceledException();
			}
			throw new IllegalStateException(createDRepresentationRunnable.getStatus().getMessage());
		}
		return Optional.empty();
	}

	/**
	 * Open representation.
	 * 
	 * @param session
	 *            Session
	 * @param representation
	 *            DRepresentation
	 * @param monitor
	 *            IProgressMonitor
	 */
	public static Optional<DDiagramEditor> openCrossLayersDiagram(Session session, DRepresentation representation,
			IProgressMonitor monitor) {
		monitor.setTaskName(org.eclipse.sirius.viewpoint.provider.Messages.CreateRepresentationAction_openingTask);
		
		// activate refresh on opening if necessary
		boolean activateRefreshOnOpening = false;
		if (session.getSiriusPreferences().isRefreshOnRepresentationOpening()) {
			activateRefreshOnOpening = true;
			  SiriusEditPlugin.getPlugin().getPreferenceStore().setValue(SiriusUIPreferencesKeys.PREF_REFRESH_ON_REPRESENTATION_OPENING.name(), true);		
		}
		
		// run diagram opening in UI Thread
		OpenRepresentationRunnable openRepresentationRunnable = new OpenRepresentationRunnable(session, representation,
				monitor);
		Display.getDefault().syncExec(openRepresentationRunnable);
		
		// re init refresh on opening if necessary
		if (activateRefreshOnOpening) {
			SiriusEditPlugin.getPlugin().getPreferenceStore().setValue(SiriusUIPreferencesKeys.PREF_REFRESH_ON_REPRESENTATION_OPENING.name(), false);
		}

		// get opened diagram.
		if (openRepresentationRunnable.getStatus().isOK()) {
			IEditorPart openedEditor = openRepresentationRunnable.getResult();
			if (openedEditor instanceof DDiagramEditor) {
				return Optional.of((DDiagramEditor) openedEditor);
			}
		} else {
			if (openRepresentationRunnable.getStatus().matches(Status.CANCEL)) {
				throw new OperationCanceledException();
			}
			throw new IllegalStateException(openRepresentationRunnable.getStatus().getMessage());
		}
		return Optional.empty();
	}

	/**
	 * Run layout actions.
	 * 
	 * @param editor
	 *            DDiagramEditor
	 * @param monitor
	 *            IProgressMonitor
	 */
	@SuppressWarnings({ "unchecked" })
	public static void runLayoutActions(DDiagramEditor editor, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 20);
		subMonitor.beginTask(Messages.CrossAnalysisRunnable_LayoutActionTaskLabel, 20);

		// get diagram edit part
		DDiagramEditPart diagramEditPart = (DDiagramEditPart) ((DiagramEditor) editor).getDiagramEditPart();
		// get all diagram edit parts.
		List<GraphicalEditPart> editParts = (List<GraphicalEditPart>) diagramEditPart.getChildren();
		// get auto size command
		CompoundCommand autosizeCommand = getAutoSizeCommand(editParts);

		// run auto size action + arrange all action in UI Thread
		Display.getDefault().syncExec(() -> {
			diagramEditPart.getDiagramEditDomain().getDiagramCommandStack().execute(autosizeCommand,
					subMonitor.split(10));
			// run arrange all action
			LayoutUtil.arrange(diagramEditPart);
			subMonitor.split(10);
		});
	}

	/**
	 * Compute auto size command.
	 * 
	 * @param editParts
	 *            List<GraphicalEditPart>
	 * @return auto size command.
	 */
	public static CompoundCommand getAutoSizeCommand(List<GraphicalEditPart> editParts) {
		CompoundCommand autosizeCommand = new CompoundCommand(Messages.SiriusUtil_AutosizeCommandName);
		Request autosizeRequest = new Request(RequestConstants.REQ_AUTOSIZE);
		editParts.forEach(ep -> {
			Command command = ep.getCommand(autosizeRequest);
			autosizeCommand.add(command);
		});
		return autosizeCommand;
	}

	/**
	 * Close and delete diagram.
	 * 
	 * @param session
	 *            session
	 * @param diagram
	 *            Optional<DSemanticDiagram>
	 * @param editor
	 *            Optional<DDiagramEditor>
	 */
	public static void closeAndDeleteDiagram(Session session, Optional<DSemanticDiagram> diagram,
			Optional<DDiagramEditor> editor) {
		if (editor.isPresent() && editor.get()
				.equals(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor())) {
			Display.getDefault().syncExec(() -> DialectUIManager.INSTANCE.closeEditor(editor.get(), false));
		}

		if (diagram.isPresent() && session != null) {
			// delete diagram
			DSemanticDiagram dSemanticDiagram = diagram.get();
			Optional<DRepresentationDescriptor> dRepresentationdescriptor = DialectManager.INSTANCE
					.getRepresentationDescriptors(dSemanticDiagram.getTarget(), session).stream()
					.filter(descriptor -> dSemanticDiagram.equals(descriptor.getRepresentation())).findFirst();
			if (dRepresentationdescriptor.isPresent()) {
				RecordingCommand deleteRepresentationCommand = new RecordingCommand(
						session.getTransactionalEditingDomain(), Messages.SiriusUtil_DeleteRepresentationCommand) {

					@Override
					protected void doExecute() {
						DialectManager.INSTANCE.deleteRepresentation(dRepresentationdescriptor.get(), session);
					}
				};
				session.getTransactionalEditingDomain().getCommandStack().execute(deleteRepresentationCommand);
			}
		}
	}

	/**
	 * Lock element.
	 */
	public static void lockElement(EObject capellaElement, Session session) {
		LockedInstanceException lockedInstanceException = PermissionRegistryUtil.lockElement(capellaElement);
		if (lockedInstanceException != null) {
			throw new IllegalStateException(PermissionUtil.getLockElementErrorMessage(session, lockedInstanceException),
					lockedInstanceException);
		}
	}

	/**
	 * Unlock element.
	 */
	public static void unlockElement(EObject capellaElement) {
		boolean unlockElement = PermissionRegistryUtil.unlockElement(capellaElement);
		if (!unlockElement) {
			throw new IllegalStateException(
					String.format(com.navalgroup.capella.permission.Messages.PermissionUtil_UnlockElement_UnlockFailed,
							PermissionUtil.getDisplayName(capellaElement)));
		}
	}
}

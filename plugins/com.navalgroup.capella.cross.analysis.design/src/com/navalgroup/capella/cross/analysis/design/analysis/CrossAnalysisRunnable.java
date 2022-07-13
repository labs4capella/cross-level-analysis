/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.analysis;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.sirius.business.api.dialect.DialectManager;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.business.api.session.SessionManager;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.diagram.ui.tools.api.editor.DDiagramEditor;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.core.data.cs.Part;

import com.navalgroup.capella.cross.analysis.design.Messages;
import com.navalgroup.capella.cross.analysis.design.services.CrossAnalysisViewpointServices;
import com.navalgroup.capella.cross.analysis.design.util.SiriusUtil;

/**
 * Cross Analysis : create Cross Layers diagram and populate it with capella
 * element and its dependencies tree structure.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class CrossAnalysisRunnable implements IRunnableWithProgress {

	/**
	 * Debug constant.
	 */
	private static final boolean DEBUG = false;

	/**
	 * Capella Element.
	 */
	private ModelElement capellaElement;

	/**
	 * Created CLD diagram.
	 */
	private Optional<DSemanticDiagram> diagram = Optional.empty();

	/**
	 * CLD diagram editor.
	 */
	private Optional<DDiagramEditor> editor = Optional.empty();

	/**
	 * Session.
	 */
	private Session session;

	/**
	 * Constructor.
	 */
	public CrossAnalysisRunnable(ModelElement capellaElement) {
		this.capellaElement = capellaElement;
		if (capellaElement instanceof Part) {
			this.capellaElement = ((Part) capellaElement).getType();
		}
	}

	/**
	 * Execute cross analysis. Init Cross Layers diagram and populate it.
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 140);
			subMonitor.beginTask(Messages.CrossAnalysisRunnable_TaskLabel, 140);
			manageCancel(monitor);

			// check session is valid
			monitor.subTask(Messages.CrossAnalysisRunnable_CheckSessionTaskLabel);
			checkSession(subMonitor);
			manageCancel(monitor);

			// get representation description and check viewpoint is activated
			monitor.subTask(Messages.CrossAnalysisRunnable_GetRepresentationDescriptionTaskLabel);
			RepresentationDescription representationDescription = getRepresentationDescription(capellaElement);
			monitor.worked(10);
			manageCancel(monitor);

			// manage lock
			monitor.subTask(Messages.CrossAnalysisRunnable_LockTaskLabel);
			SiriusUtil.lockElement(capellaElement, session);
			monitor.worked(10);
			manageCancel(monitor);

			// create representation
			monitor.subTask(Messages.CrossAnalysisRunnable_CreateDiagramTaskLabel);
			diagram = createCrossLayersDiagram(this.capellaElement, representationDescription,
					subMonitor.split(20).checkCanceled());
			manageCancel(monitor);

			if (diagram.isPresent()) {
				// populate the diagram
				// get dependencies structure tree elements
				monitor.subTask(Messages.CrossAnalysisRunnable_GetTreeStructureTaskLabel);
				Set<ModelElement> capellaElements = getDependenciesTreeStruture(this.capellaElement,
						subMonitor.split(10).checkCanceled());
				manageCancel(monitor);

				// add it in diagram
				monitor.subTask(Messages.CrossAnalysisRunnable_CreateViewTaskLabel);
				SiriusUtil.createViews(session, diagram.get(),
						CrossAnalysisUtil.getElementViewFromMappingDomainBased(capellaElements),
						subMonitor.split(20).checkCanceled());
				manageCancel(monitor);

				// open the diagram
				monitor.subTask(Messages.CrossAnalysisRunnable_OpenDiagramTaskLabel);
				editor = SiriusUtil.openCrossLayersDiagram(session, diagram.get(),
						subMonitor.split(20).checkCanceled());
				manageCancel(monitor);

				if (editor.isPresent() && editor.get() instanceof DiagramEditor) {
					monitor.subTask(Messages.CrossAnalysisRunnable_LayoutActionTaskLabel);
					SiriusUtil.runLayoutActions(editor.get(), subMonitor.split(20).checkCanceled());
					manageCancel(monitor);
				}
			}

			// manage unlock
			monitor.subTask(Messages.CrossAnalysisRunnable_UnlockTaskLabel);
			SiriusUtil.unlockElement(capellaElement);
			monitor.worked(10);
			manageCancel(monitor);
		} finally {
			monitor.done();
		}

	}

	/**
	 * Check session.
	 * 
	 * @param monitor
	 * @throws InterruptedException
	 */
	public void checkSession(IProgressMonitor monitor) throws InterruptedException {
		session = SessionManager.INSTANCE.getSession(this.capellaElement);
		if (session == null) {
			throw new IllegalStateException(Messages.CrossAnalysisRunnable_NoSessionFound);
		}
		monitor.worked(10);
	}

	/**
	 * Get dependencies tree structure from element.
	 * 
	 * @param element
	 *            ModelElement
	 * @param monitor
	 * @return dependencies tree structure from element.
	 */
	protected Set<ModelElement> getDependenciesTreeStruture(ModelElement element, IProgressMonitor monitor) {
		monitor.beginTask(Messages.CrossAnalysisRunnable_GetTreeStructureTaskLabel, 10);
		// get descendants
		Set<ModelElement> descendants = new LinkedHashSet<>();
		descendants.add(element);
		computeDescendantElements(element, descendants);
		monitor.worked(5);

		// get ancestors
		Set<ModelElement> ancestors = new LinkedHashSet<>();
		ancestors.add(element);
		computeAncestorElements(element, ancestors);
		monitor.worked(5);

		// compute all dependencies tree structure
		descendants.addAll(ancestors);
		return descendants;
	}

	/**
	 * Compute dependencies tree structure from element.
	 * 
	 * @param element
	 *            ModelElement
	 * @param allReferencedElements
	 *            List<ModelElement>
	 */
	public void computeDescendantElements(ModelElement element, Set<ModelElement> allReferencedElements) {
		List<ModelElement> referencedElements = CrossAnalysisUtil.getDescendantElements(element);
		allReferencedElements.addAll(referencedElements);
		referencedElements.forEach(el -> computeDescendantElements(el, allReferencedElements));
	}

	/**
	 * Compute ancestor tree structure from element.
	 * 
	 * @param element
	 *            ModelElement
	 * @param allReferencedElements
	 *            List<ModelElement>
	 */
	public void computeAncestorElements(ModelElement element, Set<ModelElement> allReferencedElements) {
		List<ModelElement> referencedElements = CrossAnalysisUtil.getAncestorElements(element);
		allReferencedElements.addAll(referencedElements);
		referencedElements.forEach(el -> computeAncestorElements(el, allReferencedElements));
	}

	/**
	 * Create Cross Layers Diagram.
	 * 
	 * @param element
	 *            ModelElement
	 * @param monitor
	 * @return DRepresentation
	 */
	protected Optional<DSemanticDiagram> createCrossLayersDiagram(ModelElement element,
			RepresentationDescription representationDescription, IProgressMonitor monitor) {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		subMonitor.beginTask(Messages.CrossAnalysisRunnable_CreateDiagramTaskLabel, 100);
		// get representation description
		subMonitor.split(30);

		// create representation
		return SiriusUtil.createRepresentation(element, session, representationDescription,
				CrossAnalysisUtil.CLD_NAME_PREFIX + element.getLabel(), subMonitor.split(70));
	}

	/**
	 * Get representation description.
	 * 
	 * @param element
	 *            ModelElement
	 * @return representation description
	 */
	protected RepresentationDescription getRepresentationDescription(ModelElement element) {
		Optional<RepresentationDescription> representationDescription = DialectManager.INSTANCE
				.getAvailableRepresentationDescriptions(session.getSelectedViewpoints(false), element).stream()
				.filter(rep -> CrossAnalysisViewpointServices.VIEWPOINT_NAME.equals(rep.getName())).findFirst();

		if (!representationDescription.isPresent()) {
			throw new IllegalStateException(Messages.CrossAnalysisRunnable_NoViewpointFound);
		}
		return representationDescription.get();
	}

	/**
	 * Get the CLD Diagram.
	 * 
	 * @return the CLD diagram
	 */
	public Optional<DSemanticDiagram> getDiagram() {
		return diagram;
	}

	/**
	 * Get the CLD editor.
	 * 
	 * @return the CLD editor
	 */
	public Optional<DDiagramEditor> getEditor() {
		return editor;
	}

	/**
	 * Get the session.
	 * 
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Manage cancel.
	 * 
	 * @param monitor
	 *            IProgressMonitor
	 * @throws InterruptedException
	 */
	protected void manageCancel(IProgressMonitor monitor) throws InterruptedException {
		debug();
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
	}

	/**
	 * If cancel, close and delete the diagram. Unlock if necessary.
	 */
	public void manageCancelCase() {
		SiriusUtil.closeAndDeleteDiagram(getSession(), getDiagram(), getEditor());
		SiriusUtil.unlockElement(capellaElement);
	}

	/**
	 * Debug.
	 * 
	 * @throws InterruptedException
	 */
	protected void debug() throws InterruptedException {
		if (DEBUG) {
			Thread.sleep(3000);
		}
	}

}

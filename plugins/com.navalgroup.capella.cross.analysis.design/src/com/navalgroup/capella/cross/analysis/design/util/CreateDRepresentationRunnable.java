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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.sirius.business.api.dialect.command.CreateRepresentationCommand;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ui.business.api.session.EditingSessionEvent;
import org.eclipse.sirius.ui.business.api.session.IEditingSession;
import org.eclipse.sirius.ui.business.api.session.SessionUIManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.sirius.viewpoint.description.RepresentationDescription;

import com.navalgroup.capella.cross.analysis.design.CrossAnalysisDesignPlugin;
import com.navalgroup.capella.cross.analysis.design.Messages;

/**
 * Runnable to create a representation and return it.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
class CreateDRepresentationRunnable implements RunnableWithResult<DRepresentation> {

	/**
	 * DRepresentation.
	 */
	private DRepresentation createdDRepresentation;
	/**
	 * Session.
	 */
	private Session session;
	/**
	 * RepresentationDescription.
	 */
	private RepresentationDescription representationDescription;
	/**
	 * EObject.
	 */
	private EObject element;
	/**
	 * Representation Name.
	 */
	private String representationName;
	/**
	 * IProgressMonitor.
	 */
	private IProgressMonitor monitor;

	/**
	 * Constructor.
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
	 */
	CreateDRepresentationRunnable(EObject element, Session session, RepresentationDescription representationDescription,
			String representationName, IProgressMonitor monitor) {
		super();
		this.element = element;
		this.session = session;
		this.representationDescription = representationDescription;
		this.representationName = representationName;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		SubMonitor subMonitor = SubMonitor.convert(monitor, 3);
		CreateRepresentationCommand command = new CreateRepresentationCommand(session, representationDescription,
				element, representationName, subMonitor.split(1));
		IEditingSession editingSession = SessionUIManager.INSTANCE.getUISession(session);
		subMonitor.split(1);
		editingSession.notify(EditingSessionEvent.REPRESENTATION_ABOUT_TO_BE_CREATED_BEFORE_OPENING);
		session.getTransactionalEditingDomain().getCommandStack().execute(command);
		editingSession.notify(EditingSessionEvent.REPRESENTATION_CREATED_BEFORE_OPENING);
		subMonitor.split(1);
		createdDRepresentation = command.getCreatedRepresentation();
	}

	@Override
	public DRepresentation getResult() {
		return createdDRepresentation;
	}

	@Override
	public IStatus getStatus() {
		if (createdDRepresentation == null) {
			return new Status(Status.ERROR, CrossAnalysisDesignPlugin.PLUGIN_ID,
					Messages.CreateDRepresentationRunnable_ErrorDiagramCreation);
		}
		return Status.OK_STATUS;
	}

	@Override
	public void setStatus(IStatus arg0) {
	}
}
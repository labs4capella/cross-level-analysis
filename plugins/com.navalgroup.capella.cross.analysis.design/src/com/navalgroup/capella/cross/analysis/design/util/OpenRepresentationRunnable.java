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
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.sirius.business.api.session.Session;
import org.eclipse.sirius.ui.business.api.dialect.DialectUIManager;
import org.eclipse.sirius.viewpoint.DRepresentation;
import org.eclipse.ui.IEditorPart;

import com.navalgroup.capella.cross.analysis.design.CrossAnalysisDesignPlugin;
import com.navalgroup.capella.cross.analysis.design.Messages;

/**
 * Runnable to open a representation and return the opened editor.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class OpenRepresentationRunnable implements RunnableWithResult<IEditorPart> {
	/**
	 * Session.
	 */
	private Session session;
	/**
	 * DRepresentation.
	 */
	private DRepresentation representation;
	/**
	 * IProgressMonitor.
	 */
	private IProgressMonitor monitor;

	/**
	 * Opened Editor.
	 */
	private IEditorPart openedEditor;

	/**
	 * Constructor.
	 * 
	 * @param session
	 *            Session
	 * @param representation
	 *            DRepresentation
	 * @param monitor
	 *            IProgressMonitor
	 */
	public OpenRepresentationRunnable(Session session, DRepresentation representation, IProgressMonitor monitor) {
		super();
		this.session = session;
		this.representation = representation;
		this.monitor = monitor;
	}

	@Override
	public void run() {
		openedEditor = DialectUIManager.INSTANCE.openEditor(session, representation, monitor);

	}

	@Override
	public IEditorPart getResult() {
		return openedEditor;
	}

	@Override
	public IStatus getStatus() {
		if (openedEditor == null) {
			return new Status(Status.ERROR, CrossAnalysisDesignPlugin.PLUGIN_ID,
					Messages.OpenRepresentationRunnable_ErrorDiagramOpening);
		}
		return Status.OK_STATUS;
	}

	@Override
	public void setStatus(IStatus arg0) {
	}

}

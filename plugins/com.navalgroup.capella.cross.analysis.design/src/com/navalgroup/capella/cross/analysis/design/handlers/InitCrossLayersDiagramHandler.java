/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramElementEditPart;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.polarsys.capella.common.data.modellingcore.ModelElement;

import com.navalgroup.capella.cross.analysis.design.CrossAnalysisDesignPlugin;
import com.navalgroup.capella.cross.analysis.design.Messages;
import com.navalgroup.capella.cross.analysis.design.analysis.CrossAnalysisRunnable;

/**
 * Action to initialize and populate cross layers diagram CLD.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class InitCrossLayersDiagramHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		Shell activeShell = HandlerUtil.getActiveShell(event);
		// check selection
		if (selection instanceof StructuredSelection && !selection.isEmpty()) {
			Object firstElement = ((StructuredSelection) selection).getFirstElement();
			if (firstElement instanceof IDiagramElementEditPart) {
				firstElement = ((IDiagramElementEditPart) firstElement).resolveTargetSemanticElement();
			}
			if (firstElement instanceof ModelElement) {
				// run CrossAnalysisRunnable
				ProgressMonitorDialog pmd = new ProgressMonitorDialog(activeShell);
				CrossAnalysisRunnable crossAnalysis = new CrossAnalysisRunnable((ModelElement) firstElement);
				try {
					pmd.run(true, true, crossAnalysis);
				} catch (InvocationTargetException e) {
					// catch initialization diagram errors
					if (crossAnalysis != null) {
						crossAnalysis.manageCancelCase();
					}
					displayError(activeShell, e);
				} catch (InterruptedException | OperationCanceledException e) {
					// cancel case
					Thread.currentThread().interrupt();
					if (crossAnalysis != null) {
						crossAnalysis.manageCancelCase();
					}
				}
			}
		}
		return null;
	}

	/**
	 * Display exception.
	 * 
	 * @param shell
	 *            Shell
	 * @param e
	 *            Exception
	 */
	private void displayError(Shell shell, Exception e) {
		String message = "";
		if (e.getMessage() != null) {
			message = Messages.InitCrossLayersDiagramHandler_Exception + "\n" + e.getMessage();
		} else if (e.getMessage() == null && e instanceof InvocationTargetException) {
			message = ((InvocationTargetException) e).getTargetException().getMessage();
		}
		if (message == null) {
			message = Messages.InitCrossLayersDiagramHandler_Exception;
		}
		CrossAnalysisDesignPlugin.getPlugin().logError(message, e);
		displayError(shell, message);
	}

	/**
	 * Display error message.
	 * 
	 * @param shell
	 *            Shell
	 * @param message
	 *            String
	 */
	private void displayError(Shell shell, String message) {
		Display.getDefault().asyncExec(
				() -> MessageDialog.openError(shell, Messages.InitCrossLayersDiagramHandler_ErrorTitle, message));
	}

}

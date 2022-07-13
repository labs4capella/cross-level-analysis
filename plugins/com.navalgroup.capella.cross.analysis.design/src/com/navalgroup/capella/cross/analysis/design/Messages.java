/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design;

import org.eclipse.sirius.ext.base.I18N;
import org.eclipse.sirius.ext.base.I18N.TranslatableMessage;

/**
 * I18n messages for the plug-in.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public class Messages {

	@TranslatableMessage
	public static String InitCrossLayersDiagramHandler_Exception;
	@TranslatableMessage
	public static String InitCrossLayersDiagramHandler_ErrorTitle;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_NoSessionFound;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_NoViewpointFound;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_TaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_CreateDiagramTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_GetTreeStructureTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_CreateViewTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_LayoutActionTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_OpenDiagramTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_CheckSessionTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_GetRepresentationDescriptionTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_LockTaskLabel;
	@TranslatableMessage
	public static String CrossAnalysisRunnable_UnlockTaskLabel;
	@TranslatableMessage
	public static String CreateDRepresentationRunnable_ErrorDiagramCreation;
	@TranslatableMessage
	public static String OpenRepresentationRunnable_ErrorDiagramOpening;
	@TranslatableMessage
	public static String SiriusUtil_AutosizeCommandName;
	@TranslatableMessage
	public static String SiriusUtil_CreateViewCommand;
	@TranslatableMessage
	public static String SiriusUtil_DeleteRepresentationCommand;
	@TranslatableMessage
	public static String SiriusUtil_SaveDiagramAnnotationCommand;

	static {
		// initialize resource bundle
		I18N.initializeMessages(Messages.class, CrossAnalysisDesignPlugin.INSTANCE);
	}

	private Messages() {
	}
}

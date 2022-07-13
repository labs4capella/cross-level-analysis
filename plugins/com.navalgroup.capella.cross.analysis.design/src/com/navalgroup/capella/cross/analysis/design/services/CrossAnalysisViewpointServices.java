/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.services;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.sirius.diagram.DDiagramElement;
import org.eclipse.sirius.diagram.DSemanticDiagram;
import org.eclipse.sirius.viewpoint.DSemanticDecorator;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.ctx.SystemComponent;
import org.polarsys.capella.core.data.la.LogicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.model.helpers.PartExt;

import com.navalgroup.capella.cross.analysis.design.analysis.CrossAnalysisUtil;

/**
 * Services called in Cross Analysis viewpoint (odesign).
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public final class CrossAnalysisViewpointServices {

	/**
	 * Viewpoint name.
	 */
	public static final String VIEWPOINT_NAME = "cross.analysis.diagram.cross.layers";

	/**
	 * Constructor.
	 */
	private CrossAnalysisViewpointServices() {
	}

	/**
	 * Get element descendant elements.
	 * 
	 * @param element
	 *            ModelElement
	 * @return element descendant elements.
	 */
	public static List<ModelElement> getDescendantElements(ModelElement element) {
		return CrossAnalysisUtil.getDescendantElements(element);
	}

	/**
	 * Check if abstract type part is a LogicalComponent.
	 * 
	 * @param part
	 *            Part
	 * @return if abstract type part is a LogicalComponent
	 */
	public static boolean isLogicalComponent(Part part) {
		return part.getAbstractType() instanceof LogicalComponent;
	}

	/**
	 * Check if abstract type part is a PhysicalComponent.
	 * 
	 * @param part
	 *            Part
	 * @return if abstract type part is a PhysicalComponent
	 */
	public static boolean isPhysicalComponent(Part part) {
		return part.getAbstractType() instanceof PhysicalComponent && !PartExt.isDeployed(part);
	}

	/**
	 * Check if abstract type part is a Deployment PhysicalComponent.
	 * 
	 * @param part
	 *            Part
	 * @return if abstract type part is a Deployment PhysicalComponent
	 */
	public static boolean isDeploymentPhysicalComponent(Part part) {
		return part.getAbstractType() instanceof PhysicalComponent && PartExt.isDeployed(part);
	}

	/**
	 * Check if abstract type part is a SystemActor.
	 * 
	 * @param part
	 *            Part
	 * @return if abstract type part is a SystemActor
	 */
	public static boolean isSystemActor(Part part) {
		return part.getAbstractType() instanceof SystemComponent
				&& ((SystemComponent) part.getAbstractType()).isActor();
	}

	/**
	 * Check if abstract type part is a SystemComponent.
	 * 
	 * @param part
	 *            Part
	 * @return if abstract type part is a SystemComponent
	 */
	public static boolean isSystemComponent(Part part) {
		return part.getAbstractType() instanceof SystemComponent
				&& !((SystemComponent) part.getAbstractType()).isActor();
	}

	/**
	 * Check if eObject is diagram root.
	 * 
	 * @param eObject
	 *            EObject
	 * @param view
	 * @return if eObject is diagram root.
	 */
	public static boolean isDiagramRoot(EObject eObject, DDiagramElement view) {
		if (eObject != null && view.getParentDiagram() instanceof DSemanticDiagram) {
			EObject modelElement = eObject;
			if (modelElement instanceof Part) {
				modelElement = ((Part) eObject).getAbstractType();
			}
			return modelElement.equals(((DSemanticDecorator) view.getParentDiagram()).getTarget());
		}
		return false;
	}

}

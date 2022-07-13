/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.property.testers;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.sirius.diagram.ui.edit.api.part.IDiagramElementEditPart;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.fa.AbstractFunction;
import org.polarsys.capella.core.data.interaction.AbstractCapability;

/**
 * Test selection for Initialize Cross Layers Diagram action.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class CanInitCrossLayersDiagram extends PropertyTester {

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		boolean canInit = false;
		Object element = receiver;
		if (element instanceof IDiagramElementEditPart) {
			element = ((IDiagramElementEditPart) receiver).resolveTargetSemanticElement();
		}
		if (element instanceof ModelElement) {
			canInit = canInitCrossLayerDiagram((ModelElement) element);
		}
		return canInit;
	}

	private boolean canInitCrossLayerDiagram(ModelElement element) {
		return element instanceof Component || element instanceof AbstractFunction
				|| element instanceof AbstractCapability || element instanceof Part;
	}

}

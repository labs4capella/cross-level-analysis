/******************************************************************************
 * Copyright (c) 2021 Naval Group SA.
 * All right reserved. This program and the accompanying materials are made 
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 *     Obeo - initial API and implementation
 ******************************************************************************/
package com.navalgroup.capella.cross.analysis.design.test;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.ctx.CapabilityPkg;
import org.polarsys.capella.core.data.ctx.SystemAnalysis;
import org.polarsys.capella.core.data.ctx.SystemComponentPkg;
import org.polarsys.capella.core.data.ctx.SystemFunctionPkg;
import org.polarsys.capella.core.data.la.CapabilityRealizationPkg;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.la.LogicalComponentPkg;
import org.polarsys.capella.core.data.la.LogicalFunctionPkg;
import org.polarsys.capella.core.data.oa.EntityPkg;
import org.polarsys.capella.core.data.oa.OperationalActivityPkg;
import org.polarsys.capella.core.data.oa.OperationalAnalysis;
import org.polarsys.capella.core.data.oa.OperationalCapabilityPkg;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalComponentPkg;
import org.polarsys.capella.core.data.pa.PhysicalFunctionPkg;

import com.navalgroup.capella.cross.analysis.design.analysis.CrossAnalysisRunnable;

/**
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public abstract class AbstractCrossAnalysisTest extends AbstractTest {

	protected Set<ModelElement> descendants = new LinkedHashSet<>();
	protected Set<ModelElement> ancestors = new LinkedHashSet<>();

	protected SystemEngineering systemEngineering;

	protected OperationalAnalysis operationalAnalysis;
	protected OperationalActivityPkg operationalActivityPkg;
	protected EntityPkg entityPkg;
	protected OperationalCapabilityPkg operationalCapabilityPkg;

	protected SystemAnalysis systemAnalysis;
	protected SystemFunctionPkg systemFunctionPkg;
	protected SystemComponentPkg systemComponentPkg;
	protected CapabilityPkg systemCapabilityPkg;

	protected LogicalArchitecture logicalArchitecture;
	protected LogicalFunctionPkg logicalFunctionPkg;
	protected LogicalComponentPkg logicalComponentPkg;
	protected CapabilityRealizationPkg logicalCapabilityRealizationPkg;

	protected PhysicalArchitecture physicalArchitecture;
	protected PhysicalFunctionPkg physicalFunctionPkg;
	protected PhysicalComponentPkg physicalComponentPkg;
	protected CapabilityRealizationPkg physicalCapabilityRealizationPkg;

	/**
	 * @throws CoreException
	 * @throws IOException
	 */
	@Before
	public void setUp() throws IOException, CoreException {
		super.setUp();
		systemEngineering = (SystemEngineering) this.modelRoot.getOwnedModelRoots().get(0);
		operationalAnalysis = (OperationalAnalysis) systemEngineering.getOwnedArchitectures().get(0);
		operationalActivityPkg = (OperationalActivityPkg) operationalAnalysis.getOwnedFunctionPkg();
		entityPkg = operationalAnalysis.getOwnedEntityPkg();
		operationalCapabilityPkg = (OperationalCapabilityPkg) operationalAnalysis.getOwnedAbstractCapabilityPkg();

		systemAnalysis = (SystemAnalysis) systemEngineering.getOwnedArchitectures().get(1);
		systemFunctionPkg = (SystemFunctionPkg) systemAnalysis.getOwnedFunctionPkg();
		systemComponentPkg = systemAnalysis.getOwnedSystemComponentPkg();
		systemCapabilityPkg = (CapabilityPkg) systemAnalysis.getOwnedAbstractCapabilityPkg();

		logicalArchitecture = (LogicalArchitecture) systemEngineering.getOwnedArchitectures().get(2);
		logicalFunctionPkg = (LogicalFunctionPkg) logicalArchitecture.getOwnedFunctionPkg();
		logicalComponentPkg = logicalArchitecture.getOwnedLogicalComponentPkg();
		logicalCapabilityRealizationPkg = (CapabilityRealizationPkg) logicalArchitecture
				.getOwnedAbstractCapabilityPkg();

		physicalArchitecture = (PhysicalArchitecture) systemEngineering.getOwnedArchitectures().get(3);
		physicalFunctionPkg = (PhysicalFunctionPkg) physicalArchitecture.getOwnedFunctionPkg();
		physicalComponentPkg = physicalArchitecture.getOwnedPhysicalComponentPkg();
		physicalCapabilityRealizationPkg = (CapabilityRealizationPkg) physicalArchitecture
				.getOwnedAbstractCapabilityPkg();

	}

	/**
	 * @throws CoreException
	 */
	@After
	public void tearDown() throws CoreException {
		super.tearDown();
	}

	/**
	 * @param element
	 *            ModelElement
	 */
	public void computeDescendants(ModelElement element) {
		CrossAnalysisRunnable crossAnalysisRunnable = new CrossAnalysisRunnable(element);
		descendants = new LinkedHashSet<>();
		crossAnalysisRunnable.computeDescendantElements(element, descendants);
	}

	/**
	 * @param element
	 *            ModelElement
	 */
	public void computeAncestors(ModelElement element) {
		CrossAnalysisRunnable crossAnalysisRunnable = new CrossAnalysisRunnable(element);
		ancestors = new LinkedHashSet<>();
		crossAnalysisRunnable.computeAncestorElements(element, ancestors);
	}
}

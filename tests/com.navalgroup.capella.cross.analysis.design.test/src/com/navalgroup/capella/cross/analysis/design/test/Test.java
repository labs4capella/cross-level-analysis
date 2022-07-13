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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.ctx.Capability;
import org.polarsys.capella.core.data.ctx.SystemComponent;
import org.polarsys.capella.core.data.ctx.SystemFunction;
import org.polarsys.capella.core.data.fa.AbstractFunction;
import org.polarsys.capella.core.data.la.CapabilityRealization;
import org.polarsys.capella.core.data.la.LogicalComponent;
import org.polarsys.capella.core.data.la.LogicalFunction;
import org.polarsys.capella.core.data.oa.Entity;
import org.polarsys.capella.core.data.oa.OperationalActivity;
import org.polarsys.capella.core.data.oa.OperationalCapability;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalFunction;

/**
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 *
 */
public class Test extends AbstractCrossAnalysisTest {

	@org.junit.Test
	public void testOperationalActivity() throws IOException {
		AbstractFunction provideAircraftLocalization = operationalActivityPkg.getOwnedOperationalActivities().get(0)
				.getOwnedFunctions().get(0);
		computeDescendants(provideAircraftLocalization);
		assertEquals(6, descendants.size());
		assertEquals(2, descendants.stream().filter(SystemFunction.class::isInstance).count());
		assertEquals(2, descendants.stream().filter(LogicalFunction.class::isInstance).count());
		assertEquals(2, descendants.stream().filter(PhysicalFunction.class::isInstance).count());
		computeAncestors(provideAircraftLocalization);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testOperationalCapability() throws IOException {
		OperationalCapability entertainDuringFlight = operationalCapabilityPkg.getOwnedOperationalCapabilities().get(0);
		computeDescendants(entertainDuringFlight);
		assertEquals(9, descendants.size());
		assertEquals(3, descendants.stream().filter(Capability.class::isInstance).count());
		assertEquals(6, descendants.stream().filter(CapabilityRealization.class::isInstance).count());
		computeAncestors(entertainDuringFlight);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testOperationalEntityActor() throws IOException {
		Entity entity = entityPkg.getOwnedEntities().get(0);
		computeDescendants(entity);
		assertEquals(3, descendants.size());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
		assertEquals(1,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(entity);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testOperationalEntity() throws IOException {
		Entity entity = entityPkg.getOwnedEntities().get(2);
		computeDescendants(entity);
		assertEquals(3, descendants.size());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
		assertEquals(1,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(entity);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testSystemFunction() throws IOException {
		AbstractFunction function = systemFunctionPkg.getOwnedSystemFunctions().get(0).getOwnedFunctions().get(2)
				.getOwnedFunctions().get(1);
		computeDescendants(function);
		assertEquals(2, descendants.size());
		assertEquals(1, descendants.stream().filter(LogicalFunction.class::isInstance).count());
		assertEquals(1, descendants.stream().filter(PhysicalFunction.class::isInstance).count());
		computeAncestors(function);
		assertEquals(1, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalActivity.class::isInstance).count());
	}

	@org.junit.Test
	public void testSystemCapability() throws IOException {
		Capability capability = systemCapabilityPkg.getOwnedCapabilities().get(1);
		computeDescendants(capability);
		assertEquals(2, descendants.size());
		assertEquals(2, descendants.stream().filter(CapabilityRealization.class::isInstance).count());
		computeAncestors(capability);
		assertEquals(1, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalCapability.class::isInstance).count());
	}

	@org.junit.Test
	public void testSystemActor() throws IOException {
		SystemComponent actor = systemComponentPkg.getOwnedSystemComponents().get(2);
		computeDescendants(actor);
		assertEquals(2, descendants.size());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
		assertEquals(1,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(actor);
		assertEquals(1, ancestors.size());
		assertEquals(1, ancestors.stream().filter(el -> el instanceof Entity).count());
	}

	@org.junit.Test
	public void testSystemComponent() throws IOException {
		SystemComponent component = systemComponentPkg.getOwnedSystemComponents().get(0);
		computeDescendants(component);
		assertEquals(3, descendants.size());
		assertEquals(1, descendants.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
		assertEquals(2,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(component);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testLogicalFunction() throws IOException {
		AbstractFunction function = logicalFunctionPkg.getOwnedLogicalFunctions().get(0).getOwnedFunctions().get(2)
				.getOwnedFunctions().get(1);
		computeDescendants(function);
		assertEquals(1, descendants.size());
		assertEquals(1, descendants.stream().filter(PhysicalFunction.class::isInstance).count());
		computeAncestors(function);
		assertEquals(2, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalActivity.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(SystemFunction.class::isInstance).count());
	}

	@org.junit.Test
	public void testLogicalCapability() throws IOException {
		CapabilityRealization capability = logicalCapabilityRealizationPkg.getOwnedCapabilityRealizations().get(1);
		computeDescendants(capability);
		assertEquals(1, descendants.size());
		assertEquals(1, descendants.stream().filter(CapabilityRealization.class::isInstance).count());
		computeAncestors(capability);
		assertEquals(2, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalCapability.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(Capability.class::isInstance).count());
	}

	@org.junit.Test
	public void testLogicalActor() throws IOException {
		LogicalComponent actor = logicalComponentPkg.getOwnedLogicalComponents().get(2);
		computeDescendants(actor);
		assertEquals(1, descendants.size());
		assertEquals(1,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(actor);
		assertEquals(2, ancestors.size());
		assertEquals(1, ancestors.stream().filter(el -> el instanceof Entity).count());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
	}

	@org.junit.Test
	public void testLogicalComponent() throws IOException {
		LogicalComponent component = logicalComponentPkg.getOwnedLogicalComponents().get(0);
		computeDescendants(component);
		assertEquals(2, descendants.size());
		assertEquals(2,
				descendants.stream()
						.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof PhysicalComponent)
						.count());
		computeAncestors(component);
		assertEquals(1, ancestors.size());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
	}

	@org.junit.Test
	public void testPhysicalFunction() throws IOException {
		AbstractFunction function = physicalFunctionPkg.getOwnedPhysicalFunctions().get(0).getOwnedFunctions().get(2)
				.getOwnedFunctions().get(1);
		computeDescendants(function);
		assertEquals(0, descendants.size());
		computeAncestors(function);
		assertEquals(3, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalActivity.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(SystemFunction.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(LogicalFunction.class::isInstance).count());
	}

	@org.junit.Test
	public void testPhysicalCapability() throws IOException {
		CapabilityRealization capability = physicalCapabilityRealizationPkg.getOwnedCapabilityRealizations().get(1);
		computeDescendants(capability);
		assertEquals(0, descendants.size());
		computeAncestors(capability);
		assertEquals(3, ancestors.size());
		assertEquals(1, ancestors.stream().filter(OperationalCapability.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(Capability.class::isInstance).count());
		assertEquals(1, ancestors.stream().filter(CapabilityRealization.class::isInstance).count());
	}

	@org.junit.Test
	public void testPhysicalActor() throws IOException {
		PhysicalComponent actor = physicalComponentPkg.getOwnedPhysicalComponents().get(2);
		computeDescendants(actor);
		assertEquals(0, descendants.size());
		computeAncestors(actor);
		assertEquals(3, ancestors.size());
		assertEquals(1, ancestors.stream().filter(el -> el instanceof Entity).count());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
	}

	@org.junit.Test
	public void testPhysicalComponent() throws IOException {
		PhysicalComponent component = physicalComponentPkg.getOwnedPhysicalComponents().get(0)
				.getOwnedPhysicalComponents().get(0);
		computeDescendants(component);
		assertEquals(0, descendants.size());
		computeAncestors(component);
		assertEquals(0, ancestors.size());
	}

	@org.junit.Test
	public void testDeployedPhysicalComponent() throws IOException {
		PhysicalComponent component = physicalComponentPkg.getOwnedPhysicalComponents().get(0)
				.getOwnedPhysicalComponents().get(1);
		computeDescendants(component);
		assertEquals(0, descendants.size());
		computeAncestors(component);
		assertEquals(2, ancestors.size());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof SystemComponent).count());
		assertEquals(1, ancestors.stream()
				.filter(el -> el instanceof Part && ((Part) el).getAbstractType() instanceof LogicalComponent).count());
	}
}

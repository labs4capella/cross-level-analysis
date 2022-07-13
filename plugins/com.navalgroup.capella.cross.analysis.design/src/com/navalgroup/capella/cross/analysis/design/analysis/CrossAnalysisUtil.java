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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.data.modellingcore.ModelElement;
import org.polarsys.capella.core.data.cs.Component;
import org.polarsys.capella.core.data.cs.Part;
import org.polarsys.capella.core.data.ctx.Capability;
import org.polarsys.capella.core.data.fa.AbstractFunction;
import org.polarsys.capella.core.data.la.CapabilityRealization;
import org.polarsys.capella.core.data.oa.Entity;
import org.polarsys.capella.core.data.oa.OperationalCapability;
import org.polarsys.capella.core.semantic.queries.basic.queries.AbstractFunction_realizedFunctions;
import org.polarsys.capella.core.semantic.queries.basic.queries.AbstractFunction_realizingFunctions;
import org.polarsys.capella.core.semantic.queries.basic.queries.CapabilityRealization_RealizedCapability;
import org.polarsys.capella.core.semantic.queries.basic.queries.CapabilityRealization_RealizedCapabilityRealization;
import org.polarsys.capella.core.semantic.queries.basic.queries.CapabilityRealization_RealizingCapabilityRealization;
import org.polarsys.capella.core.semantic.queries.basic.queries.CapabilityRealizedOC;
import org.polarsys.capella.core.semantic.queries.basic.queries.CapabilityRealizingCR;
import org.polarsys.capella.core.semantic.queries.basic.queries.Component_RealizedComponents;
import org.polarsys.capella.core.semantic.queries.basic.queries.Component_RealizingComponents;
import org.polarsys.capella.core.semantic.queries.basic.queries.OCapabilityRealizingCapability;

/**
 * Services for Cross Analysis. Also used by odesign services.
 * 
 * @author <a href="mailto:nathalie.lepine@obeo.fr">Nathalie Lepine</a>
 */
public final class CrossAnalysisUtil {

	/**
	 * CLD diagram name prefix.
	 */
	public static final String CLD_NAME_PREFIX = "[CLD] ";

	/**
	 * Constructor.
	 */
	private CrossAnalysisUtil() {
	}

	/**
	 * Find corresponding elements indicated by domain based mapping.
	 * 
	 * @param capellaElements
	 *            List<EObject>
	 * @return corresponding elements indicated by domain based mapping.
	 */
	public static List<? extends EObject> getElementViewFromMappingDomainBased(
			Collection<ModelElement> capellaElements) {
		List<EObject> result = new ArrayList<>();
		capellaElements.forEach(el -> {
			if (isPartDomainBasedMapping(el)) {
				result.add(((Component) el).getRepresentingParts().get(0));
			} else {
				result.add(el);
			}
		});
		return result;
	}

	/**
	 * Check if element has domainBased=Part in its mapping.
	 * 
	 * @param element
	 *            ModelElement
	 * @return if element has domainBased=Part in its mapping.
	 */
	public static boolean isPartDomainBasedMapping(EObject element) {
		return element instanceof Component && !(element instanceof Entity)
				&& !((Component) element).getRepresentingParts().isEmpty();
	}

	/**
	 * Get descendant elements from element.
	 * 
	 * @param element
	 *            ModelElement
	 * @return descendant elements from element.
	 */
	public static List<ModelElement> getDescendantElements(ModelElement element) {
		return getRealizationsRealizing(element);
	}

	/**
	 * Get ancestor elements from element.
	 * 
	 * @param element
	 *            ModelElement
	 * @return ancestor elements from element.
	 */
	public static List<ModelElement> getAncestorElements(ModelElement element) {
		return getRealizationsRealized(element);
	}

	/**
	 * Get realizing elements.
	 * 
	 * @param element
	 *            ModelElement
	 * @return realizing elements.
	 */
	@SuppressWarnings("unchecked")
	public static List<ModelElement> getRealizationsRealizing(ModelElement element) {
		List<ModelElement> temp = new ArrayList<>();
		if (element instanceof AbstractFunction) {
			temp = castList(new AbstractFunction_realizingFunctions().compute(element));
		} else if (element instanceof Component) {
			temp = castList(new Component_RealizingComponents().compute(element));
		} else if (element instanceof Capability) {
			temp = castList(new CapabilityRealizingCR().compute(element));
		} else if (element instanceof OperationalCapability) {
			temp = castList(new OCapabilityRealizingCapability().compute(element));
		} else if (element instanceof CapabilityRealization) {
			temp = castList(new CapabilityRealization_RealizingCapabilityRealization().compute(element));
		} else if (element instanceof Part) {
			temp = getRealizationsRealizing(((Part) element).getType());
		}
		return (List<ModelElement>) getElementViewFromMappingDomainBased(temp);
	}

	/**
	 * Get realized elements.
	 * 
	 * @param element
	 *            ModelElement
	 * @return realized elements.
	 */
	@SuppressWarnings("unchecked")
	public static List<ModelElement> getRealizationsRealized(ModelElement element) {
		List<ModelElement> temp = new ArrayList<>();
		if (element instanceof AbstractFunction) {
			temp = castList(new AbstractFunction_realizedFunctions().compute(element));
		} else if (element instanceof Component) {
			temp = castList(new Component_RealizedComponents().compute(element));
		} else if (element instanceof Capability) {
			temp = castList(new CapabilityRealizedOC().compute(element));
		} else if (element instanceof CapabilityRealization) {
			temp = castList(new CapabilityRealization_RealizedCapabilityRealization().compute(element));
			temp.addAll(castList(new CapabilityRealization_RealizedCapability().compute(element)));
		} else if (element instanceof Part) {
			temp = getRealizationsRealized(((Part) element).getType());
		}
		return (List<ModelElement>) getElementViewFromMappingDomainBased(temp);
	}

	@SuppressWarnings("unchecked")
	private static <T> List<T> castList(List<Object> list) {
		final List<T> res = new ArrayList<T>();

		for (Object element : list) {
			res.add((T) element);
		}

		return res;
	}

}

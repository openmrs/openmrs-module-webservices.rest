/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7;

import org.openmrs.BaseReferenceRange;
import org.openmrs.ConceptReferenceRangeContext;
import org.openmrs.Obs;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_1.ObsResource2_1;

/**
 * Resource for `obs`, supporting the new referenceRange property added in openmrs-core 2.7
 */
@Resource(name = RestConstants.VERSION_1 + "/obs", supportedClass = Obs.class, supportedOpenmrsVersions = { "2.7.* - 9.*" })
public class ObsResource2_7 extends ObsResource2_1 {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = super.getRepresentationDescription(rep);
		if (description != null) {
			description.addProperty("referenceRange", Representation.DEFAULT);
		}
		return description;
	}

	/**
	 * The reference range directly associated with the obs if there is one, otherwise the reference range
	 * associated with its concept, evaluated as of the obs's own date (so that date-relative criteria like
	 * age-at-encounter are evaluated correctly for historical results, not as of today). Unlike
	 * "referenceRange", which only reflects a direct obs-level association, this is populated from either
	 * source in a single consistent shape, so a client does not need to know or care which source it came
	 * from.
	 */
	@PropertyGetter("effectiveReferenceRange")
	public SimpleObject getEffectiveReferenceRange(Obs obs) {
		BaseReferenceRange effectiveRange = obs.getReferenceRange();
		if (effectiveRange == null) {
			effectiveRange = Context.getConceptService().getConceptReferenceRange(new ConceptReferenceRangeContext(obs));
		}
		return effectiveRange == null ? null : toSimpleObject(effectiveRange);
	}

	private SimpleObject toSimpleObject(BaseReferenceRange range) {
		SimpleObject rangeObject = new SimpleObject();
		rangeObject.add("hiNormal", range.getHiNormal());
		rangeObject.add("hiAbsolute", range.getHiAbsolute());
		rangeObject.add("hiCritical", range.getHiCritical());
		rangeObject.add("lowNormal", range.getLowNormal());
		rangeObject.add("lowAbsolute", range.getLowAbsolute());
		rangeObject.add("lowCritical", range.getLowCritical());
		return rangeObject;
	}

	@Override
	public String getResourceVersion() {
		return RestConstants2_7.RESOURCE_VERSION;
	}
}

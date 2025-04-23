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

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Obs;
import org.openmrs.ObsReferenceRange;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_1.ObsResource2_1;

/**
 * {@link Resource} for listing ObsReferenceRanges
 */

@SubResource(parent = ObsResource2_7.class, path = "referencerange", supportedClass = ObsReferenceRange.class, supportedOpenmrsVersions = "2.7.* - 9.*")
public class ObsReferenceRangeResource2_7 extends DelegatingSubResource<ObsReferenceRange, Obs, ObsResource2_1> {
    
    @Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("hiNormal");
			description.addProperty("hiAbsolute");
			description.addProperty("hiCritical");
			description.addProperty("lowNormal");
			description.addProperty("lowAbsolute");
			description.addProperty("lowCritical");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Obs getParent(ObsReferenceRange instance) {
		return instance.getObs();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(ObsReferenceRange instance, Obs obs) {
		instance.setObs(obs);
	}

	@Override
	public ObsReferenceRange newDelegate() {
		return new ObsReferenceRange();
	}

	@Override
	public ObsReferenceRange save(ObsReferenceRange delegate) {
		throw new UnsupportedOperationException("ObsReferenceRange cannot be saved");
	}

	@Override
	public PageableResult doGetAll(Obs parent, RequestContext context) throws ResponseException {
		List<ObsReferenceRange> referenceRanges = new ArrayList<ObsReferenceRange>();
		if (parent != null && parent.getReferenceRange() != null) {
			referenceRanges.add(parent.getReferenceRange());
		}
		return new NeedsPaging<ObsReferenceRange>(referenceRanges, context);
	}

	@Override
	public ObsReferenceRange getByUniqueId(String uniqueId) {
		throw new UnsupportedOperationException("ObsReferenceRange can not get by id");
	}

	@Override
	protected void delete(ObsReferenceRange delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("ObsReferenceRange can not be deleted");
	}

	@Override
	public void purge(ObsReferenceRange delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("ObsReferenceRange can not be purged");
	}
	
	@PropertyGetter("display")
	public String getDisplayString(ObsReferenceRange referenceRange) {
		return "";
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants2_7.RESOURCE_VERSION;
	}
}

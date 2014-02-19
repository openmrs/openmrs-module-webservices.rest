/**
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import org.openmrs.Order;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.OrderResource1_8;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for {@link org.openmrs.Order}
 * , supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/order", supportedClass = Order.class, supportedOpenmrsVersions = "1.10.*")
public class OrderResource1_10 extends OrderResource1_8 {
	
	@Override
	public boolean hasTypesDefined() {
		return true;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("orderNumber");
			description.addProperty("patient", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("action");
			description.addProperty("careSetting", Representation.REF);
			description.addProperty("previousOrder", Representation.REF);
			description.addProperty("startDate");
			description.addProperty("dateStopped");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter", Representation.REF);
			description.addProperty("orderer", Representation.REF);
			description.addProperty("orderReason", Representation.REF);
			description.addProperty("orderReasonNonCoded");
			description.addProperty("urgency");
			description.addProperty("instructions");
			description.addProperty("commentToFulfiller");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("orderNumber");
			description.addProperty("patient", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("action");
			description.addProperty("careSetting", Representation.DEFAULT);
			description.addProperty("previousOrder", Representation.REF);
			description.addProperty("startDate");
			description.addProperty("dateStopped");
			description.addProperty("autoExpireDate");
			description.addProperty("encounter", Representation.REF);
			description.addProperty("orderer", Representation.REF);
			description.addProperty("orderReason", Representation.REF);
			description.addProperty("orderReasonNonCoded");
			description.addProperty("urgency");
			description.addProperty("instructions");
			description.addProperty("commentToFulfiller");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		} else {
			return null;
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription d = new DelegatingResourceDescription();
		d.addRequiredProperty("patient");
		d.addRequiredProperty("concept");
		d.addRequiredProperty("action");
		d.addRequiredProperty("startDate");
		d.addRequiredProperty("careSetting");
		d.addRequiredProperty("encounter");
		d.addProperty("dateStopped");
		d.addProperty("autoExpireDate");
		d.addProperty("orderer");
		d.addProperty("previousOrder");
		d.addProperty("urgency");
		d.addProperty("orderReason");
		d.addProperty("orderReasonNonCoded");
		d.addProperty("instructions");
		d.addProperty("commentToFulfiller");
		return d;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_10.RESOURCE_VERSION;
	}
}

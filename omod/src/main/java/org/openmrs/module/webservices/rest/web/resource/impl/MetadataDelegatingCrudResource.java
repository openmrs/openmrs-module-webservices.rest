/**
 * The contents of this file are subject to the OpenMRS Public License
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
package org.openmrs.module.webservices.rest.web.resource.impl;

import java.util.Date;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Subclass of {@link DelegatingCrudResource} with helper methods specific to
 * {@link OpenmrsMetadata}
 * 
 * @param <T>
 */
public abstract class MetadataDelegatingCrudResource<T extends OpenmrsMetadata> extends DelegatingCrudResource<T> {
	
	@RepHandler(RefRepresentation.class)
	public SimpleObject convertToRef(T delegate) throws ConversionException {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display", "name", Representation.DEFAULT);
		if (delegate.isRetired())
			rep.addProperty("retired");
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(T delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("description");
		rep.addProperty("retired");
		rep.addSelfLink();
		rep.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	@RepHandler(FullRepresentation.class)
	public SimpleObject asFullRep(T delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("description");
		rep.addProperty("retired");
		rep.addProperty("auditInfo", findMethod("getAuditInfo"));
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}
	
	public SimpleObject getAuditInfo(T delegate) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(delegate, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(delegate.getDateCreated(), Representation.DEFAULT));
		if (delegate.isRetired()) {
			ret.put("retiredBy", ConversionUtil.getPropertyWithRepresentation(delegate, "retiredBy", Representation.REF));
			ret
			        .put("dateRetired", ConversionUtil.convertToRepresentation(delegate.getDateRetired(),
			            Representation.DEFAULT));
			ret.put("retireReason", ConversionUtil.convertToRepresentation(delegate.getRetireReason(),
			    Representation.DEFAULT));
		}
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(delegate, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(delegate.getDateChanged(), Representation.DEFAULT));
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(T delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isRetired()) {
			// since DELETE should be idempotent, we return success here
			return;
		}
		delegate.setRetired(true);
		delegate.setRetiredBy(Context.getAuthenticatedUser());
		delegate.setDateRetired(new Date());
		delegate.setRetireReason(reason);
		save(delegate);
	}
}

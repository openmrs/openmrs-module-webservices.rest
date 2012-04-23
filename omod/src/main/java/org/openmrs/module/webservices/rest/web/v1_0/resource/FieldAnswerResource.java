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
package org.openmrs.module.webservices.rest.web.v1_0.resource;

import org.openmrs.FieldAnswer;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link FieldAnswer}, supporting standard CRUD operations
 * TODO THIS SHOULD PROBABLY BE A SUBRESOURCE UNDER FIELD, RIGHT?
 */
@Resource("fieldanswer")
@Handler(supports = FieldAnswer.class, order = 0)
public class FieldAnswerResource extends DelegatingCrudResource<FieldAnswer> {
	
	@RepHandler(RefRepresentation.class)
	public SimpleObject asRef(FieldAnswer delegate) throws ConversionException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display", findMethod("getDisplayString"));
		description.addSelfLink();
		return convertDelegateToRepresentation(delegate, description);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("concept", Representation.REF);
			description.addProperty("field", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("concept");
			description.addProperty("field");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public FieldAnswer getByUniqueId(String uniqueId) {
		return Context.getFormService().getFieldAnswerByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	protected FieldAnswer newDelegate() {
		return new FieldAnswer();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(java.lang.Object)
	 */
	@Override
	protected FieldAnswer save(FieldAnswer delegate) {
		throw new UnsupportedOperationException("A field answer must be added to a field, not created on its own.");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(FieldAnswer delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("A field answer must be removed from a field, not purged on its own.");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<FieldAnswer> doGetAll(RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException(
		        "Field answers must be retrieved from a field or by uuid, but not on their own.");
	}
	
	@Override
	protected void delete(FieldAnswer delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("A field answer must be removed from a field, not deleted on its own.");
	}
	
	/**
	 * provide audit info for a FieldAnswer
	 * 
	 * @param delegate
	 * @return the auditInfo
	 * @throws Exception 
	 */
	public SimpleObject getAuditInfo(FieldAnswer delegate) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(delegate, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(delegate.getDateCreated(), Representation.DEFAULT));
		return ret;
	}
	
	/**
	 * @param encounter
	 * @return encounter type and date
	 */
	public String getDisplayString(FieldAnswer delegate) {
		if (delegate == null)
			return null;
		
		return new StringBuilder().append(delegate.getField() == null ? "Null Field" : delegate.getField().getName())
		        .append(" - ").append(
		            delegate.getConcept() == null ? "Null Concept" : delegate.getConcept().getName().toString()).toString();
	}
	
}

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
package org.openmrs.module.webservices.rest.web.resource;

import java.text.ParseException;
import java.util.List;
import org.openmrs.Obs;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Obs, supporting standard CRUD operations
 */
@Resource("obs")
@Handler(supports = Obs.class, order = 0)
public class ObsResource extends DataDelegatingCrudResource<Obs> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Obs delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getObsService().voidObs(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Obs getByUniqueId(String uniqueId) {
		return Context.getObsService().getObsByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			// TODO how to handle valueCodedName?
			// TODO groupMember to  be  added to the  description only if the groupMember.size()>1
			// TODO  Concept Resource  needs to be  implemeted to  get  the  representation of the concept
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("person", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("value", findMethod("getValueAsString"));
			description.addProperty("obsDatetime");
			description.addProperty("accessionNumber");
			description.addProperty("obsGroup", Representation.REF);
			description.addProperty("groupMembers", findMethod("getObsMembersDefaultRepresentation"));
			description.addProperty("comment");
			description.addProperty("location", Representation.REF);
			description.addProperty("order", Representation.REF);
			description.addProperty("encounter", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			// TODO how to handle valueCodedName?
			//TODO groupMember to  be  added to the  description only if the groupMember.size()>1
			//TODO  Concept Resource  needs to be  implemeted t  get  the  representatio of th concept
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("person", Representation.REF);
			description.addProperty("concept");
			description.addProperty("value", findMethod("getValueAsString"));
			description.addProperty("obsDatetime");
			description.addProperty("accessionNumber");
			description.addProperty("obsGroup");
			description.addProperty("groupMembers", findMethod("getObsMembersDefaultRepresentation"));
			description.addProperty("comment");
			description.addProperty("location");
			description.addProperty("order");
			description.addProperty("encounter");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	protected Obs newDelegate() {
		return new Obs();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Obs delegate, RequestContext context) throws ResponseException {
		Context.getObsService().purgeObs(delegate);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#save(java.lang.Object)
	 */
	@Override
	protected Obs save(Obs delegate) {
		return Context.getObsService().saveObs(delegate, "REST web service");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected List<Obs> doSearch(String query, RequestContext context) {
		return Context.getObsService().getObservations(query);
	}
	
	/**
	 * Display string for Obs
	 * 
	 * @param obs
	 * @return String ConceptName = value
	 */
	public String getDisplayString(Obs obs) {
		return obs.getConcept().getName() + "=" + obs.getValueAsString(Context.getLocale());
	}
	
	/**
	 * Retrives the Obs Value as string
	 * 
	 * @param obs
	 * @return
	 */
	
	public String getValueAsString(Obs obs) {
		return obs.getValueAsString(Context.getLocale());
	}
	
	/**
	 * Checks if there are more than one obs in GroupMembers and converts into a DEFAULT
	 * representation
	 * 
	 * @param obs
	 * @return Object
	 * @throws ConversionException
	 */
	public Object getObsMembersDefaultRepresentation(Obs obs) throws ConversionException {
		
		if (obs.getGroupMembers() != null && obs.getGroupMembers().size() > 1) {
			return ConversionUtil.convertToRepresentation(obs.getGroupMembers(), Representation.DEFAULT);
		}
		return null;
	}
	
	/**
	 * Checks if there are more than one obs in GroupMembers and converts into a REF representation
	 * 
	 * @param obs
	 * @return Object
	 * @throws ConversionException
	 */
	public Object getObsMembersRefRepresentation(Obs obs) throws ConversionException {
		
		if (obs.getGroupMembers() != null && obs.getGroupMembers().size() > 1) {
			return ConversionUtil.convertToRepresentation(obs.getGroupMembers(), Representation.REF);
		}
		return null;
	}
	
	/**
	 * Annotated setter for Concept
	 * 
	 * @param obs
	 * @param value
	 */
	@PropertySetter("concept")
	public static void setConcept(Obs obs, Object value) {
		obs.setConcept(Context.getConceptService().getConceptByUuid((String) value));
	}
	
	/**
	 * Annotated setter for ConceptValue
	 * 
	 * @param obs
	 * @param value
	 * @throws ParseException
	 */
	@PropertySetter("value")
	public static void setValue(Obs obs, Object value) throws ParseException {
		obs.setValueAsString((String) value);
	}
}

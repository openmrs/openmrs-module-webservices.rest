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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.Visit;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

/**
 * {@link Resource} for {@link Visit}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/visit", supportedClass = Visit.class, supportedOpenmrsVersions = "1.9.*")
public class VisitResource1_9 extends DataDelegatingCrudResource<Visit> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("patient", Representation.REF);
			description.addProperty("visitType", Representation.REF);
			description.addProperty("indication", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("startDatetime");
			description.addProperty("stopDatetime");
			description.addProperty("encounters", Representation.REF);
			description.addProperty("attributes", "activeAttributes", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("patient", Representation.REF);
			description.addProperty("visitType", Representation.REF);
			description.addProperty("indication", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("startDatetime");
			description.addProperty("stopDatetime");
			description.addProperty("encounters", Representation.DEFAULT);
			description.addProperty("attributes", "activeAttributes", Representation.DEFAULT);
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * Returns a display string
	 * 
	 * @param visit
	 * @return the display string
	 */
	public String getDisplayString(Visit visit) {
		String ret = visit.getVisitType().getName();
		ret += " ";
		ret += visit.getLocation() == null ? "?" : "@ " + visit.getLocation().getName();
		ret += " - ";
		ret += Context.getDateTimeFormat().format(visit.getStartDatetime());
		return ret;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("patient");
		description.addRequiredProperty("visitType");
		description.addRequiredProperty("startDatetime");
		
		description.addProperty("location");
		description.addProperty("indication");
		description.addProperty("stopDatetime");
		description.addProperty("encounters");
		description.addProperty("attributes");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		//shouldn't be editing the patient
		description.removeProperty("patient");
		return description;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Visit newDelegate() {
		return new Visit();
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public Visit save(Visit visit) {
		return Context.getVisitService().saveVisit(visit);
	}
	
	/**
	 * Fetches a visit by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Visit getByUniqueId(String uuid) {
		return Context.getVisitService().getVisitByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(org.openmrs.Encounter,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(Visit visit, String reason, RequestContext context) throws ResponseException {
		if (visit.isVoided()) {
			// Makes no sense, so we return success here
			return;
		}
		Context.getVisitService().voidVisit(visit, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Visit visit, RequestContext context) throws ResponseException {
		if (visit == null)
			return;
		Context.getVisitService().purgeVisit(visit);
	}
	
	/**
	 * Gets un voided visits for the given patient including inactive ones
	 * 
	 * @param patientUniqueId @see {@link PatientResource1_8#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	public SimpleObject getVisitsByPatient(String patientUniqueId, RequestContext context) throws ResponseException {
		Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceByName(RestConstants.VERSION_1 + "/patient"))
		        .getByUniqueId(patientUniqueId);
		if (patient == null)
			throw new ObjectNotFoundException();
		return new NeedsPaging<Visit>(Context.getVisitService().getVisitsByPatient(patient, true, false), context)
		        .toSimpleObject();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * Sets the attributes of a visit
	 * 
	 * @param visit the visit whose attributes to set
	 * @param attributes the attributes to set
	 */
	@PropertySetter("attributes")
	public static void setAttributes(Visit visit, Set<VisitAttribute> attributes) {
		for (VisitAttribute attribute : attributes) {
			attribute.setOwner(visit);
		}
		visit.setAttributes(attributes);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#search(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String parameter = context.getRequest().getParameter("patient");
		if (parameter != null) {
			return getVisitsByPatient(parameter, context);
		} else {
			return super.search(context);
		}
	}
}

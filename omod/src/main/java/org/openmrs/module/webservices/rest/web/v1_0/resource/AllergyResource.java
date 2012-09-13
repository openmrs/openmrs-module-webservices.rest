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

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.activelist.Allergy;
import org.openmrs.annotation.Handler;
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
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Allergy, supporting standard CRUD operations
 */
@Resource("allergy")
@Handler(supports = Allergy.class, order = 0)
public class AllergyResource extends DataDelegatingCrudResource<Allergy> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	
	@Override
	protected void delete(Allergy delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPatientService().voidAllergy(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	
	@Override
	public Allergy getByUniqueId(String uniqueId) {
		Allergy allergy = Context.getPatientService().getAllergy(Integer.parseInt(uniqueId));
		if (allergy != null) {
			allergy.setUuid(ResourceTestConstants.ALLERGY_UUID);
		}
		return allergy;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("allergyType");
			description.addProperty("reaction");
			description.addProperty("severity");
			description.addProperty("person");
			description.addProperty("activeListType");
			description.addProperty("allergen");
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("startObs");
			description.addProperty("stopObs");
			description.addProperty("comments");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("allergyType");
			description.addProperty("reaction");
			description.addProperty("severity");
			description.addProperty("person");
			description.addProperty("activeListType");
			description.addProperty("allergen");
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("startObs");
			description.addProperty("stopObs");
			description.addProperty("comments");
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("person");
		description.addRequiredProperty("allergyType");
		description.addRequiredProperty("allergen");
		description.addProperty("reaction");
		description.addProperty("severity");
		description.addProperty("comment");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	
	@Override
	public Allergy newDelegate() {
		return new Allergy();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	
	@Override
	public void purge(Allergy delegate, RequestContext context) throws ResponseException {
		Context.getPatientService().removeAllergy(delegate, "REST web service");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	
	@Override
	public Allergy save(Allergy delegate) {
		Context.getPatientService().saveAllergy(delegate);
		return null;
	}
	
	/**
	 * Display string for allergy
	 * 
	 * @param allergy
	 * @return String ConceptName
	 */
	
	public String getDisplayString(Allergy allergy) {
		if (allergy.getAllergen() == null)
			return "";
		
		return allergy.getAllergen().getName().toString();
	}
	
	/**
	 * Annotated setter for allergen
	 * 
	 * @param allergen
	 * @param value
	 */
	
	@PropertySetter("allergen")
	public static void setAllergen(Allergy allergy, Object value) {
		allergy.setAllergen(Context.getConceptService().getConceptByUuid((String) value));
	}
	
	/**
	 * Gets allergies for a given patient (paged according to context if necessary)
	 * 
	 * @param patientUuid @see {@link PatientResource#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	
	public SimpleObject getAllergiesByPatient(String patientUuid, RequestContext context) throws ResponseException {
		Patient patient = Context.getService(RestService.class).getResource(PatientResource.class)
		        .getByUniqueId(patientUuid);
		if (patient == null)
			throw new ObjectNotFoundException();
		List<Allergy> allergies = Context.getPatientService().getAllergies(patient);
		return new NeedsPaging<Allergy>(allergies, context).toSimpleObject();
	}
	
}

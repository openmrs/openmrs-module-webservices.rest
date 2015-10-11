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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import org.openmrs.Patient;
import org.openmrs.Allergy;
import org.openmrs.Allergen;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

/**
 * {@link Resource} for Allergy, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/allergy", order = 70, supportedClass = Allergy.class, supportedOpenmrsVersions = { "2.0.0" })
public class AllergyResource2_0 extends DataDelegatingCrudResource<Allergy> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("allergenType");
			description.addProperty("reaction", Representation.REF);
			description.addProperty("severity");
			description.addProperty("allergen", Representation.REF);
			description.addProperty("comments");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("allergenType");
			description.addProperty("reaction", Representation.DEFAULT);
			description.addProperty("severity");
			description.addProperty("allergen", Representation.DEFAULT);
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
		description.addRequiredProperty("patient");
		description.addProperty("comments");

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
	 * Display string for allergy
	 * 
	 * @param allergy
	 * @return String ConceptName
	 */
	@PropertyGetter("display")
	public String getDisplayString(Allergy allergy) {
		if (allergy.getAllergen() == null)
			return "";

		return allergy.getAllergen().getCodedAllergen().getName().getName();
	}

	/**
	 * Annotated setter for allergen
	 *
	 * @param allergen
	 * @param value
	 */
	@PropertySetter("allergen")
	public static void setAllergen(Allergy allergy, Object value) {
		Allergen allergen = new Allergen();
		allergen.setCodedAllergen(Context.getConceptService().getConceptByUuid(
				(String) value));
		allergy.setAllergen(allergen);
	}

	/**
	 * Gets allergies for a given patient (paged according to context if
	 * necessary) only if a patient parameter exists in the request set on the
	 * {@link RequestContext}
	 * 
	 * @param context
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(
					RestService.class).getResourceBySupportedClass(
					Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();

			return new NeedsPaging<Allergy>(Context.getPatientService()
					.getAllergies(patient), context);
		}

		// currently this is not supported since the superclass throws an
		// exception
		return super.doSearch(context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Allergy getByUniqueId(String uniqueId) {
		// TODO cannot be implemented because Allergies doesn't have a Patient
		// on it
		/*
		 * String patientUuid = context.getRequest().getParameter("patient"); if
		 * (patientUuid != null) { Patient patient =
		 * Context.getPatientService().getPatientByUuid(patientUuid); if
		 * (patient == null) return null; Allergies allergies =
		 * Context.getPatientService().getAllergies(patient); return
		 * allergies.getAllergy(Integer.parseInt(uniqueId)); }
		 */
		return null;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Allergy save(Allergy delegate) {
		Context.getPatientService().saveAllergy(delegate);
		return delegate;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Allergy delegate, String reason,
			RequestContext context) throws ResponseException {
		Context.getPatientService().voidAllergy(delegate, reason);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Allergy delegate, RequestContext context)
			throws ResponseException {
		// TODO: add a purge method
	}

}

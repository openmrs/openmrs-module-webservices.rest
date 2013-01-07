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

import org.openmrs.Patient;
import org.openmrs.activelist.Problem;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Problem, supporting standard CRUD operations
 */
@Resource(name = "problem", supportedClass = Problem.class)
public class ProblemResource extends BaseActiveListItemResource<Problem> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = super.getRepresentationDescription(rep);
			description.addProperty("modifier");
			description.addProperty("sortWeight");
			description.addProperty("problem", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = super.getRepresentationDescription(rep);
			description.addProperty("modifier");
			description.addProperty("sortWeight");
			description.addProperty("problem", Representation.DEFAULT);
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("problem");
		description.addProperty("modifier");
		description.addProperty("sortWeight");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Problem newDelegate() {
		return new Problem();
	}
	
	/**
	 * Display string for Problem
	 * 
	 * @param problem
	 * @return String ConceptName
	 */
	
	public String getDisplayString(Problem problem) {
		if (problem.getProblem() == null)
			return "";
		
		return problem.getProblem().getName().toString();
	}
	
	/**
	 * Annotated setter for Problem
	 * 
	 * @param problem
	 * @param value
	 */
	
	@PropertySetter("problem")
	public static void setProblem(Problem problem, Object value) {
		problem.setProblem(Context.getConceptService().getConceptByUuid((String) value));
	}
	
	/**
	 * Gets problems for a given patient (paged according to context if necessary)
	 * 
	 * @param patientUuid @see {@link PatientResource#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException
	 */
	
	public SimpleObject getProblemsByPatient(String patientUuid, RequestContext context) throws ResponseException {
		Patient patient = ((PatientResource) Context.getService(RestService.class)
		        .getResourceBySupportedClass(Patient.class)).getByUniqueId(patientUuid);
		if (patient == null)
			throw new ObjectNotFoundException();
		return new NeedsPaging<Problem>(Context.getPatientService().getProblems(patient), context).toSimpleObject();
	}
	
}

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
import org.openmrs.activelist.Problem;
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
 * {@link Resource} for Problem, supporting standard CRUD operations
 */
@Resource("problem")
@Handler(supports = Problem.class, order = 0)
public class ProblemResource extends DataDelegatingCrudResource<Problem> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	
	@Override
	protected void delete(Problem delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPatientService().voidProblem(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	
	@Override
	public Problem getByUniqueId(String uniqueId) {
		Problem problem = Context.getPatientService().getProblem(Integer.parseInt(uniqueId));
		if (problem != null) {
			problem.setUuid(ResourceTestConstants.PROBLEM_UUID);
		}
		return problem;
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
			description.addProperty("modifier");
			description.addProperty("sortWeight");
			description.addProperty("person");
			description.addProperty("activeListType");
			description.addProperty("problem");
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
			description.addProperty("modifier");
			description.addProperty("sortWeight");
			description.addProperty("person");
			description.addProperty("activeListType");
			description.addProperty("problem");
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
		description.addRequiredProperty("activeListType");
		description.addRequiredProperty("problem");
		description.addProperty("modifier");
		description.addProperty("sortWeight");
		description.addProperty("comments");
		
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	
	@Override
	public void purge(Problem delegate, RequestContext context) throws ResponseException {
		Context.getPatientService().removeProblem(delegate, "REST web service");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	
	@Override
	public Problem save(Problem delegate) {
		Context.getPatientService().saveProblem(delegate);
		return null;
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
		Patient patient = Context.getService(RestService.class).getResource(PatientResource.class)
		        .getByUniqueId(patientUuid);
		if (patient == null)
			throw new ObjectNotFoundException();
		List<Allergy> allergies = Context.getPatientService().getAllergies(patient);
		return new NeedsPaging<Allergy>(allergies, context).toSimpleObject();
	}
	
}

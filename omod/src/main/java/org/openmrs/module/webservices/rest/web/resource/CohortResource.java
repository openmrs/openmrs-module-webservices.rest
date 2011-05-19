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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Cohorts, supporting standard CRUD operations
 */
@Resource("cohort")
@Handler(supports = Cohort.class, order = 0)
public class CohortResource extends DataDelegatingCrudResource<Cohort> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Cohort cohort, String reason, RequestContext context) throws ResponseException {
		if (cohort.isVoided()) {
			// http operation DELETE is idempotent, so we return success here
			return;
		}
		Context.getCohortService().voidCohort(cohort, reason);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Cohort getByUniqueId(String uuid) {
		return Context.getCohortService().getCohortByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
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
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("cohortId");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("memberIds", Representation.REF);
			description.addProperty("uri", findMethod("getUri"));
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("cohortId");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("memberIds");
			description.addProperty("uri", findMethod("getUri"));
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	protected Cohort newDelegate() {
		return new Cohort();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Cohort cohort, RequestContext context) throws ResponseException {
		if (cohort == null) {
			// http operation DELETE is idempotent, so we return success here
			return;
		}
		Context.getCohortService().purgeCohort(cohort);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	protected Cohort save(Cohort cohort) {
		return Context.getCohortService().saveCohort(cohort);
	}
	
	/**
	 * Returns a non-voided cohort object by its name
	 */
	public Cohort getCohortByName(String query) {
		return Context.getCohortService().getCohort(query);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#propertiesToExposeAsSubResources()
	 */
	@Override
	protected List<String> propertiesToExposeAsSubResources() {
		return Arrays.asList("memberIds");
	}
	
	/**
	 * Returns a cohort member by cohort's uuid
	 */
	public List<Object> getCohortMembers(String uuid) {
		List<Object> ret = new ArrayList<Object>();
		Cohort parent = Context.getCohortService().getCohortByUuid(uuid);
		for (Integer patientId : parent.getMemberIds()) {
			ret.add(Context.getPatientService().getPatient(patientId));
		}
		return ret;
	}
	
}

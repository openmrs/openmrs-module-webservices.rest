/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * <p>
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * <p>
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/programenrollment", supportedClass = PatientProgram.class, supportedOpenmrsVersions = { "1.8.*, 1.9.*" }, order = 200)
public class ProgramEnrollmentResource1_8 extends
		DataDelegatingCrudResource<PatientProgram> {

	@Override
	public PatientProgram getByUniqueId(String uniqueId) {
		return Context.getProgramWorkflowService().getPatientProgramByUuid(
				uniqueId);
	}

	@PropertyGetter("display")
	public String getDisplayString(PatientProgram patientProgram) {
		return patientProgram.getProgram().getName();
	}

	@Override
	protected void delete(PatientProgram delegate, String reason,
			RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getProgramWorkflowService()
				.voidPatientProgram(delegate, reason);
	}

	@Override
	public void purge(PatientProgram delegate, RequestContext context)
			throws ResponseException {
		Context.getProgramWorkflowService().purgePatientProgram(delegate);
	}

	@Override
	public PatientProgram newDelegate() {
		return new PatientProgram();
	}

	@Override
	public PatientProgram save(PatientProgram delegate) {
		return Context.getProgramWorkflowService().savePatientProgram(delegate);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("patient", Representation.REF);
			description.addProperty("program", Representation.REF);
			description.addProperty("display");
			description.addProperty("dateEnrolled");
			description.addProperty("dateCompleted");
			description.addProperty("location", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("patient");
			description.addProperty("program");
			description.addProperty("display");
			description.addProperty("dateEnrolled");
			description.addProperty("dateCompleted");
			description.addProperty("location");
			description.addProperty("voided");
			description.addSelfLink();
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			return description;
		} else {
			return null;
		}
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription d = new DelegatingResourceDescription();
		d.addRequiredProperty("patient");
		d.addRequiredProperty("program");
		d.addRequiredProperty("dateEnrolled");

		d.addProperty("dateCompleted");
		d.addProperty("location");
		d.addProperty("voided");
		return d;
	}

	/**
	 * Gets all the programs (including voided) of the given patient
	 *
	 * @param context
	 * @return all programs of the given patient
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid != null) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				return new EmptySearchResult();
			}

			List<PatientProgram> patientPrograms = Context
					.getProgramWorkflowService().getPatientPrograms(patient,
							null, null, null, null, null, true);
			return new NeedsPaging<PatientProgram>(patientPrograms, context);
		}
		return super.doSearch(context);
	}
}

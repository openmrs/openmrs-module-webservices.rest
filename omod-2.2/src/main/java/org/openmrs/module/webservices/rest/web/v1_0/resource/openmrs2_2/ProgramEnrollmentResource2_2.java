/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_2;

import org.openmrs.PatientProgramAttribute;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.ProgramEnrollmentResource1_10;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/programenrollment", supportedClass = PatientProgram.class, supportedOpenmrsVersions = { "2.2.*" }, order = 0)
public class ProgramEnrollmentResource2_2 extends ProgramEnrollmentResource1_10 {
	
	@PropertySetter("attributes")
	public static void setAttributes(PatientProgram instance, List<PatientProgramAttribute> attrs) {
		for (PatientProgramAttribute attr : attrs) {
			instance.addAttribute(attr);
		}
	}
	
	@Override
	public PatientProgram newDelegate() {
		return new PatientProgram();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription parentRep = super.getRepresentationDescription(rep);
		if (rep instanceof DefaultRepresentation) {
			parentRep.addProperty("attributes", Representation.REF);
			return parentRep;
		} else if (rep instanceof FullRepresentation) {
			parentRep.addProperty("states", Representation.REF);
			parentRep.addProperty("attributes", Representation.DEFAULT);
			return parentRep;
		} else {
			return null;
		}
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = super.getCreatableProperties();
		delegatingResourceDescription.addProperty("attributes");
		return delegatingResourceDescription;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		DelegatingResourceDescription delegatingResourceDescription = super.getUpdatableProperties();
		delegatingResourceDescription.addProperty("attributes");
		return delegatingResourceDescription;
	}
	
	@Override
	public PatientProgram getByUniqueId(String uniqueId) {
		return Context.getProgramWorkflowService().getPatientProgramByUuid(uniqueId);
	}
	
	@Override
	protected void delete(PatientProgram delegate, String reason, RequestContext context) throws ResponseException {
		if (!delegate.isVoided().booleanValue()) {
			Context.getProgramWorkflowService().voidPatientProgram(delegate, reason);
		}
	}
	
	@Override
	public void purge(PatientProgram delegate, RequestContext context) throws ResponseException {
		Context.getProgramWorkflowService().purgePatientProgram(delegate);
	}
	
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("attributes");
	}
	
	@Override
	public PatientProgram save(PatientProgram delegate) {
		return Context.getProgramWorkflowService().savePatientProgram(delegate);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid != null) {
			PatientService patientService = Context.getPatientService();
			Patient patient = patientService.getPatientByUuid(patientUuid);
			if (patient == null) {
				return new EmptySearchResult();
			} else {
				List patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient,
				    (Program) null, (Date) null, (Date) null, (Date) null, (Date) null, true);
				return new NeedsPaging(patientPrograms, context);
			}
		} else {
			return super.doSearch(context);
		}
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
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

import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/programenrollment", supportedClass = PatientProgram.class, supportedOpenmrsVersions = { "1.8.* - 1.9.*" }, order = 1)
public class ProgramEnrollmentResource1_8 extends DataDelegatingCrudResource<PatientProgram> {
	
	@Override
	public PatientProgram getByUniqueId(String uniqueId) {
		return Context.getProgramWorkflowService().getPatientProgramByUuid(uniqueId);
	}
	
	@PropertyGetter("display")
	public String getDisplayString(PatientProgram patientProgram) {
		return patientProgram.getProgram().getName();
	}
	
	@Override
	protected void delete(PatientProgram delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getProgramWorkflowService().voidPatientProgram(delegate, reason);
	}
	
	@Override
	protected PatientProgram undelete(PatientProgram delegate, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			delegate = Context.getProgramWorkflowService().unvoidPatientProgram(delegate);
		}
		return delegate;
	}
	
	@Override
	public void purge(PatientProgram delegate, RequestContext context) throws ResponseException {
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
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
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
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
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
			description.addProperty("auditInfo");
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
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		ObjectSchema model = (ObjectSchema) super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .addProperty("uuid", new UUIDSchema())
			        .addProperty("display", new StringSchema())
			        .addProperty("dateEnrolled", new DateSchema())
			        .addProperty("dateCompleted", new DateSchema())
			        .addProperty("voided", new BooleanSchema());
		}
		if (rep instanceof DefaultRepresentation) {
			model
					.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientGet"))
					.addProperty("program", new Schema<Program>().$ref("#/components/schemas/ProgramGet"))
					.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationGet"));
		} else if (rep instanceof FullRepresentation) {
			model
					.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientGetFull"))
					.addProperty("program", new Schema<Program>().$ref("#/components/schemas/ProgramGetFull"))
					.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationGetFull"));
		}
		return model;
	}
	
	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		ObjectSchema model = (ObjectSchema) new ObjectSchema()
		        .addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientCreate").example("uuid"))
		        .addProperty("program", new Schema<Program>().$ref("#/components/schemas/ProgramCreate").example("uuid"))
		        .addProperty("dateEnrolled", new DateSchema())
		        .addProperty("dateCompleted", new DateSchema())
		        .addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationCreate").example("uuid"))
		        .addProperty("voided", new BooleanSchema());
		model.setRequired(Arrays.asList("patient", "program", "dateEnrolled"));
		if (rep instanceof FullRepresentation) {
			model
					.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientCreateFull").example("uuid"))
					.addProperty("program", new Schema<Program>().$ref("#/components/schemas/ProgramCreateFull").example("uuid"))
					.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationCreateFull").example("uuid"));
		}
		return model;
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return new ObjectSchema()
		        .addProperty("dateEnrolled", new DateSchema())
		        .addProperty("dateCompleted", new DateSchema()); //FIXME missing props
		
	}
	
	/**
	 * Gets all the programs (excluding voided) of the given patient
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
			
			List<PatientProgram> patientPrograms = Context.getProgramWorkflowService().getPatientPrograms(patient, null,
			    null, null, null, null, false);
			return new NeedsPaging<PatientProgram>(patientPrograms, context);
		}
		return super.doSearch(context);
	}
}

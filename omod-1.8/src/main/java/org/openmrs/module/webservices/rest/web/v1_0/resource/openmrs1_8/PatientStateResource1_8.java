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
import org.openmrs.PatientProgram;
import org.openmrs.PatientState;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Resource} for PatientState, supporting standard CRUD operations
 */
@SubResource(parent = ProgramEnrollmentResource1_8.class, path = "state", supportedClass = PatientState.class, supportedOpenmrsVersions = {
        "1.8.* - 9.*" })
public class PatientStateResource1_8 extends DelegatingSubResource<PatientState, PatientProgram, ProgramEnrollmentResource1_8> {
	
	@Override
	public PatientProgram getParent(PatientState instance) {
		return instance.getPatientProgram();
	}
	
	@Override
	public void setParent(PatientState instance, PatientProgram parent) {
		instance.setPatientProgram(parent);
	}
	
	@Override
	public PageableResult doGetAll(PatientProgram parent, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException("Operation not supported.");
	}
	
	@Override
	public PatientState getByUniqueId(String uniqueId) {
		return Context.getProgramWorkflowService().getPatientStateByUuid(uniqueId);
	}
	
	@Override
	protected void delete(PatientState delegate, String reason, RequestContext context) throws ResponseException {
		PatientProgram patientProgram = delegate.getPatientProgram();
		PatientState lastPatientState = getLastPatientState(delegate.getState().getProgramWorkflow(), patientProgram);
		if (lastPatientState != null) {
			if (lastPatientState.getUuid().equals(delegate.getUuid())) {
				patientProgram.voidLastState(delegate.getState().getProgramWorkflow(), Context.getAuthenticatedUser(), null,
				    null);
				Context.getProgramWorkflowService().savePatientProgram(patientProgram);
			} else {
				throw new ResourceDoesNotSupportOperationException("Only last state can be deleted");
			}
		}
	}
	
	private PatientState getLastPatientState(ProgramWorkflow currentWorkflow, PatientProgram patientProgram) {
		List<PatientState> patientStates = new ArrayList<PatientState>(patientProgram.statesInWorkflow(currentWorkflow,
		    false));
		if (!patientStates.isEmpty()) {
			sortPatientStatesBasedOnStartDate(patientStates);
			return patientStates.get(patientStates.size() - 1);
		} else {
			return null;
		}
	}
	
	private static void sortPatientStatesBasedOnStartDate(List<PatientState> patientStates) {
		Collections.sort(patientStates, new Comparator<PatientState>() {
			
			@Override
			public int compare(PatientState o1, PatientState o2) {
				return OpenmrsUtil.compareWithNullAsLatest(o1.getStartDate(), o2.getStartDate());
			}
		});
	}
	
	@Override
	public PatientState newDelegate() {
		return new PatientState();
	}
	
	@Override
	public PatientState save(PatientState delegate) {
		PatientProgram parent = delegate.getPatientProgram();
		parent.getStates().add(delegate);
		return delegate;
	}
	
	@Override
	public void purge(PatientState delegate, RequestContext context) {
		throw new ResourceDoesNotSupportOperationException("Purging of patient state not supported.");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("state", Representation.REF);
			description.addProperty("uuid");
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("voided");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("state", Representation.DEFAULT);
			description.addProperty("uuid");
			description.addProperty("patientProgram", Representation.REF);
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("state", Representation.FULL);
			description.addProperty("uuid");
			description.addProperty("patientProgram", Representation.DEFAULT);
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription d = new DelegatingResourceDescription();
		d.addRequiredProperty("state");
		return d;
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription updatableProperties = super.getUpdatableProperties();
		updatableProperties.addProperty("startDate");
		updatableProperties.addProperty("endDate");
		updatableProperties.addProperty("voided");
		return updatableProperties;
	}
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		ObjectSchema model = (ObjectSchema) super.getGETSchema(rep);
		if (rep instanceof RefRepresentation || rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .addProperty("uuid", new StringSchema())
			        .addProperty("startDate", new DateSchema())
			        .addProperty("endDate", new DateSchema())
			        .addProperty("voided", new BooleanSchema());
		}
		if (rep instanceof DefaultRepresentation) {
			model.addProperty("state", new Schema<ProgramWorkflowState>().$ref("#/components/schemas/WorkflowStateGet"));
		} else if (rep instanceof RefRepresentation) {
			model
					.addProperty("state", new Schema<ProgramWorkflowState>().$ref("#/components/schemas/WorkflowStateGetRef"))
			        .addProperty("patientProgram", new ObjectSchema()); //FIXME type
		} else if (rep instanceof FullRepresentation) {
			model
					.addProperty("state", new Schema<ProgramWorkflowState>().$ref("#/components/schemas/WorkflowStateGetFull"))
			        .addProperty("patientProgram", new ObjectSchema()); //FIXME type
		}
		return model;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
		        .addProperty("state", new Schema<ProgramWorkflowState>().$ref("#/components/schemas/WorkflowStateCreate"))
		        .required(Collections.singletonList("state"));
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return new ObjectSchema()
		        .addProperty("startDate", new DateSchema())
		        .addProperty("endDate", new DateSchema())
		        .addProperty("voided", new BooleanSchema());
	}
}

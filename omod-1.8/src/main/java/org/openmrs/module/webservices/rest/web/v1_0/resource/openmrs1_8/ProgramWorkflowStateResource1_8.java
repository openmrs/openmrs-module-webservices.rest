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
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import org.openmrs.Concept;
import org.openmrs.Program;
import org.openmrs.ProgramWorkflow;
import org.openmrs.ProgramWorkflowState;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SubResource(parent = ProgramWorkflowResource1_8.class, path = "state", supportedClass = ProgramWorkflowState.class, supportedOpenmrsVersions = {
        "1.8.* - 9.*" })
public class ProgramWorkflowStateResource1_8 extends DelegatingSubResource<ProgramWorkflowState, ProgramWorkflow, ProgramWorkflowResource1_8> {
	
	@Override
	public ProgramWorkflow getParent(ProgramWorkflowState instance) {
		return instance.getProgramWorkflow();
	}
	
	@Override
	public void setParent(ProgramWorkflowState instance, ProgramWorkflow programWorkflow) {
		instance.setProgramWorkflow(programWorkflow);
	}
	
	@Override
	public PageableResult doGetAll(ProgramWorkflow parent, RequestContext context) throws ResponseException {
		List<ProgramWorkflowState> states = new ArrayList<ProgramWorkflowState>();
		if (parent != null) {
			for (ProgramWorkflowState state : parent.getStates()) {
				states.add(state);
			}
		}
		return new NeedsPaging<ProgramWorkflowState>(states, context);
	}
	
	@Override
	public ProgramWorkflowState getByUniqueId(String uuid) {
		return Context.getProgramWorkflowService().getStateByUuid(uuid);
	}
	
	@Override
	protected void delete(ProgramWorkflowState delegate, String reason, RequestContext context) throws ResponseException {
		ProgramWorkflow parentWorkflow = getParent(delegate);
		parentWorkflow.removeState(delegate);
		Context.getProgramWorkflowService().saveProgram(parentWorkflow.getProgram());
	}

	@Override
	public ProgramWorkflowState newDelegate() {
		return new ProgramWorkflowState();
	}
	
	@Override
	public ProgramWorkflowState save(ProgramWorkflowState delegate) {
		ProgramWorkflow workflow = getParent(delegate);
		workflow.addState(delegate);
		Program program = workflow.getProgram();
		program.addWorkflow(workflow);
		Context.getProgramWorkflowService().saveProgram(program);
		return delegate;
	}
	
	@Override
	public void purge(ProgramWorkflowState delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("concept", Representation.DEFAULT);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("description");
			description.addProperty("retired");
			description.addProperty("concept", Representation.FULL);
			description.addSelfLink();
			return description;
		} else if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("retired");
			description.addProperty("concept", Representation.REF);
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> model = super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation) {
			model
			        .addProperty("uuid", new UUIDSchema())
			        .addProperty("description", new StringSchema())
			        .addProperty("retired", new BooleanSchema())
					.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGet"));
		} else if (rep instanceof FullRepresentation) {
			model
			        .addProperty("uuid", new UUIDSchema())
			        .addProperty("description", new StringSchema())
			        .addProperty("retired", new BooleanSchema())
					.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGetFull"));
		} else if (rep instanceof RefRepresentation) {
			model
			        .addProperty("uuid", new UUIDSchema())
			        .addProperty("retired", new BooleanSchema())
					.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGet"));
		}
		return model;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptCreate").required(Collections.singletonList("concept")))
				.addProperty("initial", new BooleanSchema())
				.addProperty("terminal", new BooleanSchema());

	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("concept");
		description.addProperty("initial");
		description.addProperty("terminal");
		return description;
	}
}

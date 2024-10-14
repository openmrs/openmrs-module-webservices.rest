/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.openmrs.ConceptStateConversion;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link ConceptStateConversion}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptstateconversion", supportedClass = ConceptStateConversion.class,
		supportedOpenmrsVersions = { "2.0.* - 9.*" })
public class ConceptStateConversionResource2_0 extends DelegatingCrudResource<ConceptStateConversion> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addProperty("programWorkflow", Representation.REF);
			description.addProperty("programWorkflowState", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.DEFAULT);
			description.addProperty("programWorkflow", Representation.DEFAULT);
			description.addProperty("programWorkflowState", Representation.DEFAULT);
			description.addSelfLink();
			return description;
		} else if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addProperty("programWorkflow", Representation.REF);
			description.addProperty("programWorkflowState", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		}
		return null;
	}

	@PropertyGetter("display")
	public String getDisplayProperty(ConceptStateConversion delegate) {
		return delegate.toString();
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("concept");
		description.addRequiredProperty("programWorkflow");
		description.addRequiredProperty("programWorkflowState");
		return description;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema != null) {
            if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation || rep instanceof RefRepresentation) {
				schema
						.addProperty("concept", new Schema<>().$ref("#/components/schemas/ConceptGetRef"))
						.addProperty("programWorkflow", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowGetRef")))
						.addProperty("programWorkflowState", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowStateGetRef")));
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> schema = new ObjectSchema();
		if (rep instanceof DefaultRepresentation || rep instanceof RefRepresentation) {
			schema
					.addProperty("concept", new Schema<>().$ref("#/components/schemas/ConceptCreate"))
					.addProperty("programWorkflow", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowCreate")))
					.addProperty("programWorkflowState", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowStateCreate")));
		} else if (rep instanceof FullRepresentation) {
			schema
					.addProperty("concept", new Schema<>().$ref("#/components/schemas/ConceptCreateFull"))
					.addProperty("programWorkflow", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowCreateFull")))
					.addProperty("programWorkflowState", new ArraySchema().items(new Schema<>().$ref("#/components/schemas/WorkflowStateGet")));
		}
		return schema;
	}

	@Override
	public ConceptStateConversion newDelegate() {
		return new ConceptStateConversion();
	}

	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<>(Context.getProgramWorkflowService().getAllConceptStateConversions(), context);
	}

	@Override
	public ConceptStateConversion getByUniqueId(String uniqueId) {
		return Context.getProgramWorkflowService().getConceptStateConversionByUuid(uniqueId);
	}

	@Override
	public ConceptStateConversion save(ConceptStateConversion delegate) {
		if (!delegate.getProgramWorkflow().getConcept().getUuid().equals(delegate.getConcept().getUuid())) {
			throw new IllegalRequestException("Program Workflow must belong to the same Concept as State Conversion");
		}
		if (!delegate.getProgramWorkflowState().getProgramWorkflow().getConcept().getUuid().equals(delegate.getConcept().getUuid())) {
			throw new IllegalRequestException("Program Workflow State must belong to the same Concept as State Conversion");
		}

		return Context.getProgramWorkflowService().saveConceptStateConversion(delegate);
	}

	@Override
	public void delete(ConceptStateConversion delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void purge(ConceptStateConversion delegate, RequestContext context) throws ResponseException {
		Context.getProgramWorkflowService().purgeConceptStateConversion(delegate);
	}
}

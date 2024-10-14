/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.Drug;
import org.openmrs.DrugReferenceMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;

@Resource(name = RestConstants.VERSION_1
        + "/drugreferencemap", supportedClass = DrugReferenceMap.class, supportedOpenmrsVersions = { "1.10.* - 9.*" })
public class DrugReferenceMapResource1_10 extends DelegatingCrudResource<DrugReferenceMap> {
	
	@Override
	public DrugReferenceMap newDelegate() {
		return new DrugReferenceMap();
	}
	
	@Override
	public DrugReferenceMap save(DrugReferenceMap delegate) {
		delegate.getDrug().addDrugReferenceMap(delegate);
		Context.getConceptService().saveDrug(delegate.getDrug());
		return delegate;
	}
	
	@Override
	public DrugReferenceMap getByUniqueId(String uniqueId) {
		return Context.getService(RestHelperService.class).getObjectByUuid(DrugReferenceMap.class, uniqueId);
	}
	
	@Override
	protected void delete(DrugReferenceMap delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(DrugReferenceMap delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public PageableResult doGetAll(RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@PropertyGetter("display")
	public String getDisplayString(DrugReferenceMap map) {
		if (map.getDrug().getDisplayName() == null) {
			return "";
		}
		return map.getDrug().getDisplayName() + " - " + map.getConceptMapType().getName();
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("drug", Representation.REF);
			description.addProperty("conceptReferenceTerm", Representation.REF);
			description.addProperty("conceptMapType", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("auditInfo");
			description.addProperty("drug", Representation.DEFAULT);
			description.addProperty("conceptReferenceTerm", Representation.DEFAULT);
			description.addProperty("conceptMapType", Representation.DEFAULT);
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("conceptReferenceTerm");
		description.addRequiredProperty("conceptMapType");
		description.addProperty("drug");
		return description;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema != null) {
            schema
					.addProperty("display", new StringSchema())
					.addProperty("uuid", new StringSchema());

			if (rep instanceof DefaultRepresentation) {
				schema
						.addProperty("drug", new Schema<Drug>().$ref("#/components/schemas/DrugGetRef"))
						.addProperty("conceptReferenceTerm", new Schema<ConceptReferenceTerm>().$ref("#/components/schemas/ConceptreferencetermGetRef"))
						.addProperty("conceptMapType", new Schema<ConceptMapType>().$ref("#/components/schemas/ConceptmaptypeGetRef"));
			} else if (rep instanceof FullRepresentation) {
				schema
						.addProperty("auditInfo", new StringSchema())
						.addProperty("drug", new Schema<Drug>().$ref("#/components/schemas/DrugGet"))
						.addProperty("conceptReferenceTerm", new Schema<ConceptReferenceTerm>().$ref("#/components/schemas/ConceptreferencetermGet"))
						.addProperty("conceptMapType", new Schema<ConceptMapType>().$ref("#/components/schemas/ConceptmaptypeGet"));
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("conceptReferenceTerm", new StringSchema().example("uuid"))
				.addProperty("conceptMapType", new StringSchema().example("uuid"))
				.addProperty("drug", new StringSchema().example("uuid"));
	}
}

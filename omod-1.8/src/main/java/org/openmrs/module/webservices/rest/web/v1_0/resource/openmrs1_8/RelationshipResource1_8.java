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
import org.openmrs.Person;
import org.openmrs.Relationship;
import org.openmrs.RelationshipType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Provider, supporting
 * standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/relationship", supportedClass = Relationship.class, supportedOpenmrsVersions = { "1.8.*" })
public class RelationshipResource1_8 extends DataDelegatingCrudResource<Relationship> {
	
	@Override
	public Relationship getByUniqueId(String uuid) {
		return Context.getPersonService().getRelationshipByUuid(uuid);
	}
	
	@Override
	protected void delete(Relationship delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			return;
		}
		Context.getPersonService().voidRelationship(delegate, reason);
	}
	
	@Override
	protected Relationship undelete(Relationship delegate, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			delegate = Context.getPersonService().unvoidRelationship(delegate);
		}
		return delegate;
	}
	
	@Override
	public Relationship newDelegate() {
		return new Relationship();
	}
	
	@Override
	public Relationship save(Relationship delegate) {
		return Context.getPersonService().saveRelationship(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(org.openmrs.Encounter,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Relationship relationship, RequestContext context) throws ResponseException {
		if (relationship == null) {
			return;
		}
		Context.getPersonService().purgeRelationship(relationship);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("personA", Representation.REF, Person.class);
			description.addProperty("relationshipType", Representation.REF);
			description.addProperty("personB", Representation.REF, Person.class);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("personA", Representation.DEFAULT, Person.class);
			description.addProperty("relationshipType", Representation.DEFAULT);
			description.addProperty("personB", Representation.DEFAULT, Person.class);
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @param relationship
	 * @return relationship type and start date
	 */
	@PropertyGetter("display")
	public String getDisplayString(Relationship relationship) {
		String relType = relationship.getRelationshipType() == null ? "NULL" : relationship.getRelationshipType()
		        .getaIsToB();
		return relationship.getPersonA().getGivenName() + " is the " + relType + " of "
		        + relationship.getPersonB().getGivenName();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("personA");
		description.addRequiredProperty("relationshipType");
		description.addRequiredProperty("personB");
		description.addProperty("startDate");
		description.addProperty("endDate");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Relationship> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Relationship>(Context.getPersonService().getAllRelationships(), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		//shouldn't be editing the patient
		description.removeProperty("personA");
		description.removeProperty("personB");
		description.addProperty("voided");
		return description;
	}
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> model = super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .addProperty("uuid", new UUIDSchema())
			        .addProperty("display", new StringSchema())
			        .addProperty("voided", new BooleanSchema());
		}
		if (rep instanceof DefaultRepresentation) {
			model
					.addProperty("personA", new Schema<Person>().$ref("#/components/schemas/PersonGet"))
					.addProperty("relationshipType", new Schema<RelationshipType>().$ref("#/components/schemas/RelationshiptypeGet"))
					.addProperty("personB", new Schema<Person>().$ref("#/components/schemas/PersonGet"));
		} else if (rep instanceof FullRepresentation) {
			model
					.addProperty("personA", new Schema<Person>().$ref("#/components/schemas/PersonGetFull"))
					.addProperty("relationshipType", new Schema<RelationshipType>().$ref("#/components/schemas/RelationshiptypeGetFull"))
					.addProperty("personB", new Schema<Person>().$ref("#/components/schemas/PersonGetFull"));
		}
		return model;
	}
	
	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> model = (ObjectSchema) new ObjectSchema()
		        .addProperty("personA", new Schema<Person>().$ref("#/components/schemas/PersonCreate").example("uuid"))
		        .addProperty("relationshipType", new Schema<RelationshipType>().$ref("#/components/schemas/RelationshiptypeCreate").example("uuid"))
		        .addProperty("personB", new Schema<Person>().$ref("#/components/schemas/PersonCreate").example("uuid"))
		        .addProperty("startDate", new DateSchema())
		        .addProperty("endDate", new DateSchema());
		model.setRequired(Arrays.asList("personA", "relationshipType", "personB"));
		if (rep instanceof FullRepresentation) {
			model
					.addProperty("personA", new Schema<Person>().$ref("#/components/schemas/PersonCreate").example("uuid"))
					.addProperty("relationshipType", new Schema<RelationshipType>().$ref("#/components/schemas/RelationshiptypeCreate").example("uuid"))
					.addProperty("personB", new Schema<Person>().$ref("#/components/schemas/PersonCreate").example("uuid"));
		}
		return model;
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return new ObjectSchema()
		        .addProperty("voided", new BooleanSchema()); //FIXME missing properties
	}
}

/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Person;
import org.openmrs.Relationship;
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

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for
 * Provider, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/relationship", order = 200, supportedClass = Relationship.class, supportedOpenmrsVersions = { "1.9.*" })
public class RelationshipResource1_8 extends
		DataDelegatingCrudResource<Relationship> {

	@Override
	public Relationship getByUniqueId(String uuid) {
		return Context.getPersonService().getRelationshipByUuid(uuid);
	}

	@Override
	protected void delete(Relationship delegate, String reason,
			RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			return;
		}
		Context.getPersonService().voidRelationship(delegate, reason);
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
	public void purge(Relationship relationship, RequestContext context)
			throws ResponseException {
		if (relationship == null) {
			return;
		}
		Context.getPersonService().purgeRelationship(relationship);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description
					.addProperty("personA", Representation.REF, Person.class);
			description.addProperty("relationshipType", Representation.REF);
			description
					.addProperty("personB", Representation.REF, Person.class);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("personA", Representation.DEFAULT,
					Person.class);
			description.addProperty("relationshipType", Representation.DEFAULT);
			description.addProperty("personB", Representation.DEFAULT,
					Person.class);
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
		String relType = relationship.getRelationshipType() == null ? "NULL"
				: relationship.getRelationshipType().getaIsToB();
		return relationship.getPersonA().getGivenName() + " is the " + relType
				+ " of " + relationship.getPersonB().getGivenName();
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
	protected NeedsPaging<Relationship> doGetAll(RequestContext context)
			throws ResponseException {
		return new NeedsPaging<Relationship>(Context.getPersonService()
				.getAllRelationships(), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties()
			throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super
				.getUpdatableProperties();
		// shouldn't be editing the patient
		description.removeProperty("personA");
		description.removeProperty("personB");
		description.addProperty("voided");
		return description;
	}
}

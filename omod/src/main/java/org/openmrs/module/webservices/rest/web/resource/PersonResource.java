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

import org.openmrs.Person;
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
 * TODO: Rework
 * Just a placeholder representation. Was required for User (https://tickets.openmrs.org/browse/RESTWS-16)
 */
@Resource("person")
@Handler(supports = Person.class, order = 0)
public class PersonResource extends DataDelegatingCrudResource<Person> {
	
	public PersonResource() {
		
	}
	
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
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath", Representation.REF);
			description.addProperty("preferredName", "personName", Representation.REF);
			description.addProperty("personAddress", Representation.REF);
			description.addProperty("activeAttributes", Representation.REF);
			description.addProperty("uri", findMethod("getUri"));
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath");
			description.addProperty("preferredName", "personName", Representation.DEFAULT);
			description.addProperty("personAddress");
			description.addProperty("names");
			description.addProperty("addresses");
			description.addProperty("attributes");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addProperty("uri", findMethod("getUri"));
			return description;
		}
		return null;
	}
	
	@Override
	public Person getByUniqueId(String uuid) {
		return Context.getPersonService().getPersonByUuid(uuid);
	}
	
	@Override
	protected Person newDelegate() {
		return new Person();
	}
	
	@Override
	protected Person save(Person person) {
		return Context.getPersonService().savePerson(person);
	}
	
	@Override
	protected void delete(Person delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	@Override
	public void purge(Person delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	/**
	 * @param person
	 * @return identifier + name (for concise display purposes)
	 */
	public String getDisplayString(Person person) {
		return person.getPersonName().getFullName();
	}
}

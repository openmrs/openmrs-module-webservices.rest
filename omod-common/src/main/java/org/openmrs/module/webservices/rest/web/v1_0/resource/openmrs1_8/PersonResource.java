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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.OpenmrsUtil;

/**
 * {@link Resource} for Person, supporting standard CRUD operations
 */
@Resource(name = "person", order = 1, supportedClass = Person.class)
//order must be greater than that for PatientResource(order=0) RESTWS-273
public class PersonResource extends DataDelegatingCrudResource<Person> {
	
	public PersonResource() {
		remappedProperties.put("attributes", "activeAttributes");
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath", Representation.REF);
			description.addProperty("preferredName", "personName", Representation.REF);
			description.addProperty("preferredAddress", "personAddress", Representation.REF);
			description.addProperty("attributes", "activeAttributes", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath");
			description.addProperty("preferredName", "personName", Representation.DEFAULT);
			description.addProperty("preferredAddress", "personAddress", Representation.DEFAULT);
			description.addProperty("names");
			description.addProperty("addresses");
			description.addProperty("attributes", "activeAttributes", Representation.DEFAULT);
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("names");
		description.addRequiredProperty("gender");
		description.addProperty("age");
		description.addProperty("birthdate");
		description.addProperty("birthdateEstimated");
		description.addProperty("dead");
		description.addProperty("deathDate");
		description.addProperty("causeOfDeath");
		description.addProperty("addresses");
		description.addProperty("attributes");
		return description;
	}
	
	/**
	 * @throws ResourceDoesNotSupportOperationException 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		description.removeProperty("age");
		description.addProperty("preferredName");
		description.addProperty("preferredAddress");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("names", "addresses", "attributes");
	}
	
	/**
	 * Returns non-voided names for a person
	 * 
	 * @param instance
	 * @return
	 */
	@PropertyGetter("names")
	public static Set<PersonName> getNames(Person instance) {
		return RestUtil.removeVoidedData(instance.getNames());
	}
	
	/**
	 * Sets names and marks the first one as preferred if none is marked. It also makes sure that
	 * only one name is marked as preferred and changes the rest to non-preferred.
	 * <p>
	 * It takes the list so that the order is preserved.
	 * 
	 * @param instance
	 * @param names
	 */
	@PropertySetter("names")
	public static void setNames(Person instance, List<PersonName> names) {
		boolean hasPreferred = false;
		for (PersonName name : names) {
			if (name.isPreferred()) {
				if (!hasPreferred) {
					hasPreferred = true;
				} else {
					name.setPreferred(false);
				}
			}
		}
		
		if (!hasPreferred) {
			names.iterator().next().setPreferred(true);
		}
		
		//Hibernate expects java.util.SortedSet
		instance.setNames(new TreeSet<PersonName>(names));
	}
	
	/**
	 * Sets attributes on the given person.
	 * 
	 * @param instance
	 * @param names
	 */
	@PropertySetter("attributes")
	public static void setAttributes(Person instance, List<PersonAttribute> attrs) {
		for (PersonAttribute attr : attrs)
			instance.addAttribute(attr);
	}
	
	/**
	 * Returns non-voided addresses for a person
	 * 
	 * @param instance
	 * @return
	 */
	@PropertyGetter("addresses")
	public static Set<PersonAddress> getAddresses(Person instance) {
		return RestUtil.removeVoidedData(instance.getAddresses());
	}
	
	/**
	 * Sets addresses and marks the first one as preferred if none is marked. It also makes sure
	 * that only one address is marked as preferred and changes the rest to non-preferred.
	 * <p>
	 * It takes the list so that the order is preserved.
	 * 
	 * @param instance
	 * @param addresses
	 */
	@PropertySetter("addresses")
	public static void setAddresses(Person instance, List<PersonAddress> addresses) {
		boolean hasPreferred = false;
		for (PersonAddress address : addresses) {
			if (address.isPreferred()) {
				if (!hasPreferred) {
					hasPreferred = true;
				} else {
					address.setPreferred(false);
				}
			}
		}
		
		if (!hasPreferred) {
			addresses.iterator().next().setPreferred(true);
		}
		
		//Hibernate expects java.util.SortedSet
		instance.setAddresses(new TreeSet<PersonAddress>(addresses));
	}
	
	/**
	 * Sets the preferred name for a person. If no name exists new name is set as preferred.
	 * 
	 * @param instance
	 * @param name
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("preferredName")
	public static void setPreferredName(Person instance, PersonName name) throws ResourceDoesNotSupportOperationException {
		if (name.getId() == null) {
			throw new ResourceDoesNotSupportOperationException("Only an existing name can be marked as preferred!");
		}
		
		// switching which name is preferred
		for (PersonName existing : instance.getNames()) {
			if (existing.isVoided())
				continue;
			if (existing.isPreferred() && !existing.equals(name))
				existing.setPreferred(false);
		}
		name.setPreferred(true);
		instance.addName(name);
	}
	
	@PropertySetter("age")
	public static void setAge(Person person, Integer age) throws ResourceDoesNotSupportOperationException {
		if (person.getBirthdate() == null && age != null) {
			person.setBirthdateFromAge(age, new Date());
			person.setBirthdateEstimated(true);
		}
	}
	
	/**
	 * Sets the preferred address for a person. If no address exists new address is set as
	 * preferred.
	 * 
	 * @param instance
	 * @param name
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("preferredAddress")
	public static void setPreferredAddress(Patient instance, PersonAddress address)
	        throws ResourceDoesNotSupportOperationException {
		if (address.getPersonAddressId() == null) {
			throw new ResourceDoesNotSupportOperationException("Only an exsiting address can be markes as preferred!");
		}
		
		//un mark the current preferred address as preferred if any
		for (PersonAddress existing : instance.getAddresses()) {
			if (existing.isVoided())
				continue;
			if (existing.isPreferred() && !OpenmrsUtil.nullSafeEquals(existing, address))
				existing.setPreferred(false);
		}
		address.setPreferred(true);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Person getByUniqueId(String uuid) {
		return Context.getPersonService().getPersonByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Person newDelegate() {
		return new Person();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public Person save(Person person) {
		return Context.getPersonService().savePerson(person);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Person> doSearch(String query, RequestContext context) {
		return new NeedsPaging<Person>(Context.getPersonService().getPeople(query, null), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Person person, String reason, RequestContext context) throws ResponseException {
		if (person.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPersonService().voidPerson(person, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Person person, RequestContext context) throws ResponseException {
		if (person == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPersonService().purgePerson(person);
	}
	
	/**
	 * @param person
	 * @return fullname (for concise display purposes)
	 */
	public String getDisplayString(Person person) {
		if (person.getPersonName() == null)
			return "";
		
		return person.getPersonName().getFullName();
	}
}

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

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * {@link Resource} for Person, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/person", order = 200, supportedClass = Person.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
// order must be greater than that for PatientResource(order=0) RESTWS-273
public class PersonResource1_8 extends DataDelegatingCrudResource<Person> {

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
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath", Representation.REF);
			description.addProperty("preferredName", "personName",
					Representation.REF);
			description.addProperty("preferredAddress", "personAddress",
					Representation.REF);
			description.addProperty("attributes", "activeAttributes",
					Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath");
			description.addProperty("preferredName", "personName",
					Representation.DEFAULT);
			description.addProperty("preferredAddress", "personAddress",
					Representation.DEFAULT);
			description.addProperty("names");
			description.addProperty("addresses");
			description.addProperty("attributes", "activeAttributes",
					Representation.DEFAULT);
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
	 * @throws org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException
	 *
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties()
			throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("age");
		description.addProperty("gender");
		description.addProperty("birthdate");
		description.addProperty("birthdateEstimated");
		description.addProperty("preferredName");
		description.addProperty("preferredAddress");
		description.addProperty("addresses");
		description.addProperty("attributes");
		description.addRequiredProperty("names");
		description.addRequiredProperty("causeOfDeath");
		description.addRequiredProperty("dead");
		description.addProperty("deathDate");
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
	 * Sets names and marks the first one as preferred if none is marked. It
	 * also makes sure that only one name is marked as preferred and changes the
	 * rest to non-preferred.
	 * <p/>
	 * It takes the list so that the order is preserved.
	 *
	 * @param instance
	 * @param names
	 */
	@PropertySetter("names")
	public static void setNames(Person instance, List<PersonName> names) {
		for (PersonName existingName : instance.getNames()) {
			existingName.setPreferred(false);
		}
		setFirstNameAsPreferred(names);
		for (PersonName name : names) {
			PersonName existingName = getMatchingName(name, instance.getNames());
			if (existingName != null) {
				copyNameFields(existingName, name);
			} else {
				instance.addName(name);
			}
		}
	}

	/**
	 * Returns non-voided attributes of a person
	 *
	 * @param instance
	 */
	@PropertyGetter("attributes")
	public static List<PersonAttribute> getAttributes(Person instance) {
		return instance.getActiveAttributes();
	}

	/**
	 * Sets attributes on the given person.
	 *
	 * @param instance
	 * @param attrs
	 */
	@PropertySetter("attributes")
	public static void setAttributes(Person instance,
			List<PersonAttribute> attrs) {
		for (PersonAttribute attr : attrs) {
			PersonAttribute existingAttribute = instance.getAttribute(Context
					.getPersonService().getPersonAttributeTypeByUuid(
							attr.getAttributeType().getUuid()));
			if (existingAttribute != null) {
				if (attr.getValue() == null) {
					instance.removeAttribute(existingAttribute);
				} else {
					existingAttribute.setValue(attr.getValue());
				}
			} else {
				instance.addAttribute(attr);
			}
		}
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
	 * Sets addresses and marks the first one as preferred if none is marked. It
	 * also makes sure that only one address is marked as preferred and changes
	 * the rest to non-preferred.
	 * <p/>
	 * It takes the list so that the order is preserved.
	 *
	 * @param instance
	 * @param addresses
	 */
	@PropertySetter("addresses")
	public static void setAddresses(Person instance,
			List<PersonAddress> addresses) {
		for (PersonAddress existingAddress : instance.getAddresses()) {
			existingAddress.setPreferred(false);
		}
		setFirstAddressAsPreferred(addresses);
		for (PersonAddress address : addresses) {
			PersonAddress existingAddress = getMatchingAddress(address,
					instance.getAddresses());
			if (existingAddress != null) {
				copyAddressFields(existingAddress, address);
			} else {
				instance.addAddress(address);
			}
		}
	}

	/**
	 * Sets the preferred name for a person. If no name exists new name is set
	 * as preferred.
	 * 
	 * @param instance
	 * @param name
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("preferredName")
	public static void setPreferredName(Person instance, PersonName name)
			throws ResourceDoesNotSupportOperationException {
		if (name.getId() == null) {
			throw new ResourceDoesNotSupportOperationException(
					"Only an existing name can be marked as preferred!");
		}

		// switching which name is preferred
		for (PersonName existing : instance.getNames()) {
			if (existing.isPreferred() && !existing.equals(name))
				existing.setPreferred(false);
		}
		name.setPreferred(true);
		instance.addName(name);
	}

	@PropertyGetter("preferredName")
	public static PersonName getPreferredName(Person instance) {
		return instance.getPersonName();
	}

	@PropertySetter("age")
	public static void setAge(Person person, Integer age)
			throws ResourceDoesNotSupportOperationException {
		if (person.getBirthdate() == null && age != null) {
			person.setBirthdateFromAge(age, new Date());
			person.setBirthdateEstimated(true);
		}
	}

	/**
	 * Sets the preferred address for a person. If no address exists new address
	 * is set as preferred.
	 * 
	 * @param instance
	 * @param address
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("preferredAddress")
	public static void setPreferredAddress(Person instance,
			PersonAddress address)
			throws ResourceDoesNotSupportOperationException {
		if (address.getPersonAddressId() == null) {
			throw new ResourceDoesNotSupportOperationException(
					"Only an existing address can be marked as preferred!");
		}

		// un mark the current preferred address as preferred if any
		for (PersonAddress existing : instance.getAddresses()) {
			if (existing.isPreferred()
					&& !OpenmrsUtil.nullSafeEquals(existing, address))
				existing.setPreferred(false);
		}
		address.setPreferred(true);

	}

	@PropertyGetter("preferredAddress")
	public static PersonAddress getPreferredAddress(Person instance) {
		return instance.getPersonAddress();
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Person> doSearch(RequestContext context) {
		return new NeedsPaging<Person>(Context.getPersonService().getPeople(
				context.getParameter("q"), null), context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Person person, String reason, RequestContext context)
			throws ResponseException {
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
	public void purge(Person person, RequestContext context)
			throws ResponseException {
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
	@PropertyGetter("display")
	public String getDisplayString(Person person) {
		// TODO copy what is done in PatientResource to use configured name
		// layout
		if (person.getPersonName() == null)
			return "";

		return person.getPersonName().getFullName();
	}

	private static void copyNameFields(PersonName existingName,
			PersonName personName) {
		existingName.setPreferred(personName.getPreferred());
		existingName.setPrefix(personName.getPrefix());
		existingName.setGivenName(personName.getGivenName());
		existingName.setMiddleName(personName.getMiddleName());
		existingName.setFamilyNamePrefix(personName.getFamilyNamePrefix());
		existingName.setFamilyName(personName.getFamilyName());
		existingName.setFamilyName2(personName.getFamilyName2());
		existingName.setFamilyNameSuffix(personName.getFamilyNameSuffix());
		existingName.setDegree(personName.getDegree());
	}

	private static void setFirstNameAsPreferred(List<PersonName> personNames) {
		boolean hasPreferred = false;
		for (PersonName name : personNames) {
			if (name.isPreferred()) {
				if (!hasPreferred) {
					hasPreferred = true;
				} else {
					name.setPreferred(false);
				}
			}
		}
		if (!hasPreferred) {
			personNames.iterator().next().setPreferred(true);
		}
	}

	private static PersonName getMatchingName(PersonName personName,
			Set<PersonName> personNames) {
		for (PersonName existingName : personNames) {
			String uuid = personName.getUuid();
			if (uuid != null && uuid.equals(existingName.getUuid())) {
				return existingName;
			}
		}
		return null;
	}

	private static PersonAddress getMatchingAddress(
			PersonAddress personAddress, Set<PersonAddress> personAddresses) {
		for (PersonAddress existingAddress : personAddresses) {
			if (personAddress.getUuid().equals(existingAddress.getUuid())) {
				return existingAddress;
			}
		}
		return null;
	}

	private static void copyAddressFields(PersonAddress existingAddress,
			PersonAddress address) {
		existingAddress.setPreferred(address.getPreferred());
		existingAddress.setAddress1(address.getAddress1());
		existingAddress.setAddress2(address.getAddress2());
		existingAddress.setAddress3(address.getAddress3());
		existingAddress.setAddress4(address.getAddress4());
		existingAddress.setAddress5(address.getAddress5());
		existingAddress.setAddress6(address.getAddress6());
		existingAddress.setCityVillage(address.getCityVillage());
		existingAddress.setCountry(address.getCountry());
		existingAddress.setStateProvince(address.getStateProvince());
		existingAddress.setCountyDistrict(address.getCountyDistrict());
		existingAddress.setPostalCode(address.getPostalCode());
		existingAddress.setLatitude(address.getLatitude());
		existingAddress.setLongitude(address.getLongitude());
		existingAddress.setDateCreated(address.getDateCreated());
	}

	private static void setFirstAddressAsPreferred(List<PersonAddress> addresses) {
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
	}

	/**
	 * Overrides the base getAuditInfo(delegate) since the dateCreated for
	 * person should get personDateCreated attribute
	 *
	 * @param person
	 *            the delegate person
	 * @return audit information
	 * @throws Exception
	 */
	@Override
	public SimpleObject getAuditInfo(Person person) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(person,
				"creator", Representation.REF));
		ret.put("dateCreated",
				ConversionUtil.convertToRepresentation(
						person.getPersonDateCreated(), Representation.DEFAULT));
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(
				person, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(
				person.getDateChanged(), Representation.DEFAULT));
		if (person.isVoided()) {
			ret.put("voidedBy", ConversionUtil.getPropertyWithRepresentation(
					person, "voidedBy", Representation.REF));
			ret.put("dateVoided",
					ConversionUtil.convertToRepresentation(
							person.getDateVoided(), Representation.DEFAULT));
			ret.put("voidReason",
					ConversionUtil.convertToRepresentation(
							person.getVoidReason(), Representation.DEFAULT));
		}
		return ret;
	}
}

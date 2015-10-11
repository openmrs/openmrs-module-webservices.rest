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
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * {@link Resource} for PersonAddress, supporting standard CRUD operations
 */
@SubResource(parent = PersonResource1_8.class, path = "address", order = 200, supportedClass = PersonAddress.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PersonAddressResource1_8 extends
		DelegatingSubResource<PersonAddress, Person, PersonResource1_8> {

	public PersonAddressResource1_8() {
		allowedMissingProperties.add("startDate");
		allowedMissingProperties.add("endDate");
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("preferred");
			description.addProperty("address1");
			description.addProperty("address2");
			description.addProperty("cityVillage");
			description.addProperty("stateProvince");
			description.addProperty("country");
			description.addProperty("postalCode");
			description.addProperty("countyDistrict");
			description.addProperty("address3");
			description.addProperty("address4");
			description.addProperty("address5");
			description.addProperty("address6");
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("latitude");
			description.addProperty("longitude");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("preferred");
			description.addProperty("address1");
			description.addProperty("address2");
			description.addProperty("cityVillage");
			description.addProperty("stateProvince");
			description.addProperty("country");
			description.addProperty("postalCode");
			description.addProperty("latitude");
			description.addProperty("longitude");
			description.addProperty("countyDistrict");
			description.addProperty("address3");
			description.addProperty("address4");
			description.addProperty("address5");
			description.addProperty("address6");
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("latitude");
			description.addProperty("longitude");
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
		description.addProperty("preferred");
		description.addProperty("address1");
		description.addProperty("address2");
		description.addProperty("cityVillage");
		description.addProperty("stateProvince");
		description.addProperty("country");
		description.addProperty("postalCode");
		description.addProperty("latitude");
		description.addProperty("longitude");
		description.addProperty("countyDistrict");
		description.addProperty("address3");
		description.addProperty("address4");
		description.addProperty("address5");
		description.addProperty("address6");
		description.addProperty("startDate");
		description.addProperty("endDate");
		description.addProperty("latitude");
		description.addProperty("longitude");
		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public PersonAddress getByUniqueId(String uuid) {
		return Context.getPersonService().getPersonAddressByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(PersonAddress address, String reason,
			RequestContext context) throws ResponseException {
		// /API had no void methods as of 1.8 other
		// we should be calling voidPersonAddress that was added in 1.9
		address.setVoided(true);
		address.setVoidedBy(Context.getAuthenticatedUser());
		address.setDateVoided(new Date());
		address.setVoidReason(reason);
		Context.getPersonService().savePerson(address.getPerson());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(PersonAddress address, RequestContext context)
			throws ResponseException {
		// /API has no void methods as of 1.8 and earlier versios,
		// we should be calling voidPersonAddress(PersonAddress, Reason) that
		// was added in 1.9
		address.getPerson().removeAddress(address);
		Context.getPersonService().savePerson(address.getPerson());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public PersonAddress save(PersonAddress newAddress) {
		// make sure that the name has actually been added to the person
		boolean needToAdd = true;
		for (PersonAddress pa : newAddress.getPerson().getAddresses()) {
			if (pa.equals(newAddress)) {
				needToAdd = false;
				break;
			}
		}

		if (needToAdd) {
			newAddress.getPerson().addAddress(newAddress);
		}

		Context.getPersonService().savePerson(newAddress.getPerson());

		return newAddress;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public PersonAddress newDelegate() {
		return new PersonAddress();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Person getParent(PersonAddress instance) {
		return instance.getPerson();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(PersonAddress instance, Person parent) {
		instance.setPerson(parent);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<PersonAddress> doGetAll(Person parent,
			RequestContext context) throws ResponseException {
		// We don't return voided addresses
		List<PersonAddress> nonVoidedAddresses = new ArrayList<PersonAddress>(
				parent.getAddresses().size());
		for (PersonAddress personAddress : parent.getAddresses()) {
			if (!personAddress.isVoided())
				nonVoidedAddresses.add(personAddress);
		}
		return new NeedsPaging<PersonAddress>(nonVoidedAddresses, context);
	}

	/**
	 * Gets the display string for a person address.
	 * 
	 * @param address
	 *            the address object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(PersonAddress address) {
		return address.getAddress1();
	}
}

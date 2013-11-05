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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Person;
import org.openmrs.PersonAddress;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PersonResource1_8;

import java.util.List;
import java.util.Set;

/**
 * {@link org.openmrs.module.webservices.rest.web.annotation.Resource} for Person, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/person", order = 1, supportedClass = Person.class, supportedOpenmrsVersions = {"1.9.*"})
public class PersonResource1_9 extends PersonResource1_8 {

    /**
     * @throws org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException
     *
     * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
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
        return description;
    }

    /**
     * Sets attributes on the given person.
     *
     * @param instance
     * @param attrs
     */
    @PropertySetter("attributes")
    public static void setAttributes(Person instance, List<PersonAttribute> attrs) {
        for (PersonAttribute attr : attrs) {
            PersonAttribute existingAttribute = instance.getAttribute(Context.getPersonService().getPersonAttributeTypeByUuid(attr.getAttributeType().getUuid()));
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
     * Sets names and marks the first one as preferred if none is marked. It also makes sure that
     * only one name is marked as preferred and changes the rest to non-preferred.
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

    private static void copyNameFields(PersonName existingName, PersonName personName) {
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

    private static PersonName getMatchingName(PersonName personName, Set<PersonName> personNames) {
        for (PersonName existingName : personNames) {
            if (personName.equals(existingName)) {
                return existingName;
            }
        }
        return null;
    }

    /**
     * Sets addresses and marks the first one as preferred if none is marked. It also makes sure
     * that only one address is marked as preferred and changes the rest to non-preferred.
     * <p/>
     * It takes the list so that the order is preserved.
     *
     * @param instance
     * @param addresses
     */
    @PropertySetter("addresses")
    public static void setAddresses(Person instance, List<PersonAddress> addresses) {
        for (PersonAddress existingAddress : instance.getAddresses()) {
            existingAddress.setPreferred(false);
        }
        setFirstAddressAsPreferred(addresses);
        for (PersonAddress address : addresses) {
            PersonAddress existingAddress = getMatchingAddress(address, instance.getAddresses());
            if (existingAddress != null) {
                copyAddressFields(existingAddress, address);
            } else {
                instance.addAddress(address);
            }
        }
    }

    private static PersonAddress getMatchingAddress(PersonAddress personAddress, Set<PersonAddress> personAddresses) {
        for (PersonAddress existingAddress : personAddresses) {
            if (personAddress.equals(existingAddress)) {
                return existingAddress;
            }
        }
        return null;
    }

    private static void copyAddressFields(PersonAddress existingAddress, PersonAddress address) {
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
        existingAddress.setStartDate(address.getStartDate());
        existingAddress.setEndDate(address.getEndDate());
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

}

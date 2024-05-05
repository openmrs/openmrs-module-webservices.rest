/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_7;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.PersonResource1_11;

@Resource(name = RestConstants.VERSION_1 + "/person", order = 0, supportedClass = Person.class, supportedOpenmrsVersions = {
        "2.7.* - 9.*" })
public class PersonResource2_7 extends PersonResource1_11 {

    /**
     * Gets the display string for a person name.
     *
     * @param person the person whose person name will be displayed.
     * @return fullName (for concise display purposes)
     */
    @PropertyGetter("display")
    public String getDisplayString(Person person) {
        if (person.getPersonName() == null) {
            return "";
        }

        PersonName personName = person.getPersonName();
        try {
            NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();
            if (nameTemplate != null) {
                return nameTemplate.format(personName);
            }
        } catch (Exception e) {
            // fall through to just returning full name if no format method found or format fails
        }

        // otherwise, just return full name
        return person.getPersonName().getFullName();
    }
}

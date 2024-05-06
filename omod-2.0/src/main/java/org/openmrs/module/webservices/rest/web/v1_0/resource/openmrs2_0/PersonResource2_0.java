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

import java.lang.reflect.Method;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.PersonResource1_11;

@Resource(name = RestConstants.VERSION_1 + "/person", order = 0, supportedClass = Person.class, supportedOpenmrsVersions = {
        "2.0.* - 2.1.*" })
public class PersonResource2_0 extends PersonResource1_11 {

    @Override
    public String getDisplayString(Person person) {
        if (person.getPersonName() == null)
            return "";

        PersonName personName = person.getPersonName();
        try {
            NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();

            if (nameTemplate != null) {
                // need to use reflection since the format method was not added until later versions of openmrs
                Method format = NameTemplate.class.getDeclaredMethod("format", PersonName.class);
                return (String) format.invoke(nameTemplate, personName);
            }
        }
        catch (Exception e) {
            // fall through to just returning full name if no format method found or format fails
        }

        return personName.getFullName();
    }
}

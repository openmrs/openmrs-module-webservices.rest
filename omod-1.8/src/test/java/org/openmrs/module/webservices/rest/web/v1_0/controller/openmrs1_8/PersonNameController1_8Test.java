/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertThat;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class PersonNameController1_8Test extends MainResourceControllerTest {

    private PersonService service;

    @Before
    public void before() throws Exception {
        executeDataSet("PersonControllerTest-otherPersonData.xml");
        this.service = Context.getPersonService();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "person" + "/" + RestTestConstants1_8.PERSON_UUID + "/name";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        int count = 0;

        Person person = service.getPersonByUuid(RestTestConstants1_8.PERSON_UUID);

        for (PersonName name : person.getNames()) {
            if (!name.isVoided()) {
                count++;
            }
        }

        return count;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants1_8.PERSON_NAME_UUID;
    }

    @Test
    public void shouldSetPreferred() throws Exception {
        PersonName nonPreferred = service.getPersonNameByUuid(getUuid());
        Person person = nonPreferred.getPerson();
        assertThat(nonPreferred.isPreferred(), is(true));

        SimpleObject newName= new SimpleObject();
        newName.add("givenName", "Moses");
        newName.add("familyName", "Mujuzi");
        newName.add("preferred", "true");

        String json = new ObjectMapper().writeValueAsString(newName);

        MockHttpServletRequest req = request(RequestMethod.POST, getURI());
        req.setContent(json.getBytes());

        SimpleObject result = deserialize(handle(req));
        Object uuid = PropertyUtils.getProperty(result, "uuid");
        PersonName preferred = service.getPersonNameByUuid(uuid.toString());

        // sanity check
        assertThat(nonPreferred.isPreferred(), is(false));
        assertThat(preferred.isPreferred(), is(true));

        // check before change
        assertThat(person.getNames(), (Matcher) hasItem(allOf(
                hasProperty("preferred", is(true)),
                hasProperty("givenName", is("Moses"))
        )));

        // change non preferred to preferred
        SimpleObject updateToTrue = new SimpleObject();
        updateToTrue.add("preferred", "true");

        String updateJson = new ObjectMapper().writeValueAsString(updateToTrue);

        MockHttpServletRequest req1 = request(RequestMethod.POST, getURI() + "/" + nonPreferred.getUuid());
        req1.setContent(updateJson.getBytes());
        handle(req1);

        // check after change
        assertThat(person.getNames(), (Matcher) hasItem(allOf(
                hasProperty("preferred", is(false)),
                hasProperty("givenName", is("Moses"))
        )));
    }
}

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

import junit.framework.Assert;
import org.junit.Test;
import org.openmrs.Person;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.junit.Assert.assertFalse;

public class PersonResource1_8Test extends BaseDelegatingResourceTest<PersonResource1_8, Person> {
	
	@Override
	public Person newObject() {
		return Context.getPersonService().getPersonByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropEquals("gender", getObject().getGender());
		assertPropEquals("age", getObject().getAge());
		assertPropEquals("birthdate", getObject().getBirthdate());
		assertPropEquals("birthdateEstimated", getObject().getBirthdateEstimated());
		assertPropEquals("dead", getObject().getDead());
		assertPropEquals("deathDate", getObject().getDeathDate());
		assertPropPresent("causeOfDeath");
		assertPropPresent("preferredName");
		assertPropPresent("preferredAddress");
		assertPropPresent("attributes");
		assertPropEquals("voided", getObject().getVoided());
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropEquals("gender", getObject().getGender());
		assertPropEquals("age", getObject().getAge());
		assertPropEquals("birthdate", getObject().getBirthdate());
		assertPropEquals("birthdateEstimated", getObject().getBirthdateEstimated());
		assertPropEquals("dead", getObject().getDead());
		assertPropEquals("deathDate", getObject().getDeathDate());
		assertPropPresent("causeOfDeath");
		assertPropPresent("preferredName");
		assertPropPresent("preferredAddress");
		assertPropPresent("names");
		assertPropPresent("addresses");
		assertPropPresent("attributes");
		assertPropEquals("voided", getObject().getVoided());
		assertPropPresent("auditInfo");
	}
	
	@Override
	public String getDisplayProperty() {
		return "Mr. Horatio Test Hornblower Esq.";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.PERSON_UUID;
	}

    @Test
    public void getAttributes_shouldReturnAllAttributes() throws Exception {
        PersonResource1_8 resource = getResource();

        List<PersonAttribute> attributes1 = (List<PersonAttribute>) PersonResource1_8.getAttributes(resource.getByUniqueId("df8ae447-6745-45be-b859-403241d9913c"));
        Assert.assertEquals(2, attributes1.size());

        List<PersonAttribute> attributes2 = (List<PersonAttribute>) PersonResource1_8.getAttributes(resource.getByUniqueId("341b4e41-790c-484f-b6ed-71dc8da222de"));
        Assert.assertEquals(3, attributes2.size());
    }

    /**
     * @see {@link https://issues.openmrs.org/browse/RESTWS-426}
     * @throws Exception
     */
    @Test
    public void testCorrectResourceForPatient() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("q", "Che");
        RequestContext context = RestUtil.getRequestContext(request, new MockHttpServletResponse());

        SimpleObject simple = getResource().search(context);
        List<SimpleObject> results = (List<SimpleObject>) simple.get("results");

        assertFalse("A non-empty list is expected.", results.isEmpty());
        for (SimpleObject result : results) {
            String selfLink = findSelfLink(result);
            assertFalse("Resource should be person, but is " + selfLink, selfLink.contains("/patient/"));
        }
    }

}

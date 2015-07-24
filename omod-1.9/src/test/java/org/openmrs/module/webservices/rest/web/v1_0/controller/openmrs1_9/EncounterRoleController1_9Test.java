package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterRole;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

public class EncounterRoleController1_9Test extends MainResourceControllerTest {

    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants1_9.TEST_DATASET);
    }

    @Test
    public void createEncounterRole_shouldCreateANewEncounterRole() throws Exception {
        long before = getAllCount();
        String json = "{ \"name\": \"Taking Sample\", \"description\":\"A new role for taking sample\" }";

        handle(newPostRequest(getURI(), json));
        Assert.assertEquals(before + 1, getAllCount());
    }

    @Test
    public void voidEncounterRole_shouldRetireAEncounterRole() throws Exception {
        EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(getUuid());

        Assert.assertNotNull(encounterRole);
        Assert.assertFalse(encounterRole.getName(),encounterRole.isRetired());

        MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + getUuid());
        request.addParameter("reason", "unit test");
        handle(request);

        encounterRole = Context.getEncounterService().getEncounterRoleByUuid(getUuid());

        Assert.assertTrue(encounterRole.isRetired());
        Assert.assertEquals("unit test", encounterRole.getRetireReason());
    }

    @Test
    public void encounterRole_shouldUnretireEncounterRole() throws Exception {
        EncounterRole encounterRole = Context.getEncounterService().getEncounterRoleByUuid(getUuid());
        Context.getEncounterService().retireEncounterRole(encounterRole,"Unit test to Unretire");

        Assert.assertTrue(encounterRole.isRetired());

        String json = "{\"retired\": \"false\" }";
        handle(newPostRequest(getURI() + "/" + getUuid(), json));
        encounterRole = Context.getEncounterService().getEncounterRoleByUuid(getUuid());
        Assert.assertFalse(encounterRole.isRetired());
    }

    @Override
    public String getURI() {
        return "encounterrole";
    }

    @Override
    public String getUuid() {
        return EncounterRole.UNKNOWN_ENCOUNTER_ROLE_UUID;
    }

    @Override
    public long getAllCount() {
        return Context.getEncounterService().getAllEncounterRoles(true).size();
    }
}
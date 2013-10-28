package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.HashMap;

public class PersonAttributeResource1_8Test extends BaseModuleWebContextSensitiveTest {

    public static final String PERSON_ATTRIBUTE_JSON = "{" +
            "    \"value\": \"Bangalore\"," +
            "    \"attributeType\": {" +
            "        \"uuid\": \"54fc8400-1683-4d71-a1ac-98d40836ff7c\"" +
            "    }" +
            "}";
    private SimpleObject personAttributeSimpleObject = new SimpleObject();

    private PersonAttributeResource1_8 resource;

    @Before
    public void beforeEachTests() throws Exception {
        personAttributeSimpleObject.putAll(new ObjectMapper().readValue(PERSON_ATTRIBUTE_JSON, HashMap.class));
        resource = (PersonAttributeResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(PersonAttribute.class);
    }

    @Test
    public void shouldCreatePersonAttribute() throws Exception {
        SimpleObject created = (SimpleObject) resource.create("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", personAttributeSimpleObject, new RequestContext());
        Assert.assertEquals("Bangalore", created.get("value"));
    }

}

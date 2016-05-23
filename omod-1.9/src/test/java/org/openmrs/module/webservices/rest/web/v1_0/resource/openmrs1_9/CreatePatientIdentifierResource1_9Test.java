package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.HashMap;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientIdentifierResource1_8;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class CreatePatientIdentifierResource1_9Test extends BaseModuleWebContextSensitiveTest {
    
    private PatientIdentifierResource1_8 resource;

    @Before
    public void beforeEachTests() throws Exception {
        resource = (PatientIdentifierResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(PatientIdentifier.class);
    }

    @Test
    public void shouldCreatePatientIdentifier_WhenTypeIsSpecifiedByUuid() throws Exception {
        
         String personAttributeJson = "{" +
                "            \"identifier\": \"101-6\"," +
                "            \"identifierType\": {" +
                "              \"uuid\" : \"1a339fe9-38bc-4ab3-b180-320988c0b968\"" +
                "            }," +
                "            \"location\" : {" +
                "              \"uuid\" : \"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\"" +
                "            }," +
                "            \"preferred\": true" +
                "        }";

        SimpleObject personAttributeSimpleObject = new SimpleObject();
        personAttributeSimpleObject.putAll(new ObjectMapper().readValue(personAttributeJson, HashMap.class));

        SimpleObject created = (SimpleObject) resource.create("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", personAttributeSimpleObject, new RequestContext());
        Assert.assertEquals("101-6", created.get("identifier"));
    }

    @Test
    public void shouldCreatePatientIdentifier_WhenTypeIsSpecifiedByName() throws Exception {
        String personAttributeJson = "{" +
                "            \"identifier\": \"101-6\"," +
                "            \"identifierType\": {" +
                "              \"name\" : \"OpenMRS Identification Number\"" +
                "            }," +
                "            \"location\" : {" +
                "              \"uuid\" : \"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\"" +
                "            }," +
                "            \"preferred\": true" +
                "        }";

        SimpleObject personAttributeSimpleObject = new SimpleObject();
        personAttributeSimpleObject.putAll(new ObjectMapper().readValue(personAttributeJson, HashMap.class));

        SimpleObject created = (SimpleObject) resource.create("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", personAttributeSimpleObject, new RequestContext());
        Assert.assertEquals("101-6", created.get("identifier"));
    }

}

package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.HashMap;

public class CreatePatientIdentifierResource1_8Test extends BaseModuleWebContextSensitiveTest {
    
    private PatientIdentifierResource1_8 resource;

    @Before
    public void beforeEachTests() throws Exception {
        resource = (PatientIdentifierResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(PatientIdentifier.class);
    }

    @Test
    public void shouldCreatePatientIdentifier_WhenTypeIsSpecifiedByUuid() throws Exception {
        
         String personAttributeJson = "{" +
                "            \"identifier\": \"OpenMRS-ID-123\"," +
                "            \"identifierType\": {" +
                "              \"uuid\" : \"1a339fe9-38bc-4ab3-b180-320988c0b968\"" +
                "            }," +
                "            \"location\" : {" +
                "              \"uuid\" : \"dc5c1fcc-0459-4201-bf70-0b90535ba362\"" +
                "            }," +
                "            \"preferred\": true" +
                "        }";

        SimpleObject personAttributeSimpleObject = new SimpleObject();
        personAttributeSimpleObject.putAll(new ObjectMapper().readValue(personAttributeJson, HashMap.class));

        SimpleObject created = (SimpleObject) resource.create("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", personAttributeSimpleObject, new RequestContext());
        Assert.assertEquals("OpenMRS-ID-123", created.get("identifier"));
    }

    @Test
    public void shouldCreatePatientIdentifier_WhenTypeIsSpecifiedByName() throws Exception {
        String personAttributeJson = "{" +
                "            \"identifier\": \"OpenMRS-ID-123\"," +
                "            \"identifierType\": {" +
                "              \"name\" : \"OpenMRS Identification Number\"" +
                "            }," +
                "            \"location\" : {" +
                "              \"uuid\" : \"dc5c1fcc-0459-4201-bf70-0b90535ba362\"" +
                "            }," +
                "            \"preferred\": true" +
                "        }";

        SimpleObject personAttributeSimpleObject = new SimpleObject();
        personAttributeSimpleObject.putAll(new ObjectMapper().readValue(personAttributeJson, HashMap.class));

        SimpleObject created = (SimpleObject) resource.create("da7f524f-27ce-4bb2-86d6-6d1d05312bd5", personAttributeSimpleObject, new RequestContext());
        Assert.assertEquals("OpenMRS-ID-123", created.get("identifier"));
    }

}

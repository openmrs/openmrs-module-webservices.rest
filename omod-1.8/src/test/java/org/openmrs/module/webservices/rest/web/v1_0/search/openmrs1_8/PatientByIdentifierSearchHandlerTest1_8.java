package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class PatientByIdentifierSearchHandlerTest1_8 extends MainResourceControllerTest {


    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "patient";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return Context.getPatientService().getAllPatients(false).size();
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants1_8.PATIENT_UUID;
    }

    /**
     * @verifies return location by tag uuid
     * @see LocationSearchHandler#getSearchConfig()
     */
    @Test
    public void getSearchConfig_shouldReturnPatientByIdentifier() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("identifier", "7TU-8");

        SimpleObject result = deserialize(handle(req));
        List<Object> hits = (List<Object>) result.get("results");
        Assert.assertEquals(Context.getPatientService().getPatient(8).getUuid(), PropertyUtils.getProperty(hits.get(0), "uuid"));
    }
}

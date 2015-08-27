package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.annotations.SourceType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.SetOverrideType;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FormResourceController1_9Test extends MainResourceControllerTest {
    @Before
    public void before() throws Exception {
        executeDataSet(RestTestConstants1_9.TEST_DATASET);
        executeDataSet(RestTestConstants1_9.FORM_RESOURCE_DATA_SET);
    }

    @Test
    public void createResource_shouldUplaodNewResourceForAForm() throws Exception {
        File file = new File("omod-1.9/src/test/resources/formResourcefile.txt");
        Assert.assertTrue(file.exists());

        String fileData = OpenmrsUtil.getFileAsString(file);
        long before = getAllCount();
        String json = "{\"form\": \"df887dee-1350-11df-a1f1-0026b9348838\","+
                      " \"datatype\": \"org.openmrs.customdatatype.datatype.LongFreeTextDatatype\"," +
                      " \"value\": \"" + StringEscapeUtils.escapeJava(fileData) + " \"" +
                      "}";
        MockHttpServletResponse response = handle(newPostRequest(getURI(), json));

        Assert.assertEquals(MockHttpServletResponse.SC_CREATED, response.getStatus());
        Assert.assertEquals(before+1, getAllCount());
    }

    @Test
    public void doSearch_shouldReturnListOfFormResources() throws Exception{
        MockHttpServletRequest request = newGetRequest(getURI());
        request.setContentType("application/json");

        MockHttpServletResponse response = handle(newGetRequest(getURI()));
        System.out.println(response.getContentAsString());
        System.out.println(response.getContentType());
    }

    @Override
    public String getURI() {
        return "formresource";
    }

    @Override
    public String getUuid() {
        return RestTestConstants1_9.FORM_RESOURCE_UUID;
    }

    @Override
    public long getAllCount() {
        List<FormResource> list = new ArrayList<FormResource>();
        List<Form> forms = Context.getFormService().getAllForms();
        int count = 0;
        for(Form f:forms) {
            count += Context.getFormService().getFormResourcesForForm(f).size();
        }
        return count;
    }
}

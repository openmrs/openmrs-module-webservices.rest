package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.ClobDatatypeStorageController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.util.DatabaseUpdater;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

public class ClobDatatypeStorageControllerTest extends MainResourceControllerTest {

    @InjectMocks
    private ClobDatatypeStorageController clobController;

    @Before
    public void before() throws Exception{
        executeDataSet(RestTestConstants1_9.FORM_RESOURCE_DATA_SET);
    }

    @Test
    public void create_shouldAcceptAndStoreClobDataViaPost() throws Exception{
        long before = getAllCount();
        System.out.println(before);

        File file = new File("omod-1.9/src/test/resources/formResourcefile.txt");
        Assert.assertTrue(file.exists());

        MockMultipartFile toUpload = new MockMultipartFile("value", "formresource.txt", "text/plain",
                OpenmrsUtil.getFileAsBytes(file));

        MockMultipartHttpServletRequest request = new MockMultipartHttpServletRequest();
        request.setRequestURI(getBaseRestURI()+getURI());
        request.setMethod(RequestMethod.POST.name());
        request.setContentType("multipart/form-data");
        request.addHeader("Content-Type", "multipart/form-data");
//        request.setContent(OpenmrsUtil.getFileAsBytes(file));
//        request.addHeader("Content-Disposition", "form-data; name=\"value\"");

        request.addFile(toUpload);
//        System.out.println(request.getHeader("Content-Disposition"));

        MockHttpServletResponse response = handle(request);


//        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(clobController).build();
//        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/clobdata").file(toUpload)).andExpect(status().isCreated());

//        Assert.assertEquals(MockHttpServletResponse.SC_CREATED, response.getStatus());
//        Assert.assertEquals(before+1, getAllCount());
        System.out.println(getAllCount());

    }

    @Override
    public String getURI() {
        return "clobdata";
    }

    @Override
    public String getUuid() {
        return RestTestConstants1_9.CLOBDATATYPESTORAGE_RESOURCE_UUID;
    }

    @Override
    public long getAllCount() {
        long count = 0;
        try {
            Connection connection = DatabaseUpdater.getConnection();
            ResultSet set = connection.prepareStatement("select count('id') from clob_datatype_storage").executeQuery();
            count = set.getLong(0);
        } catch (Exception e){

        }
        return count;
    }
}

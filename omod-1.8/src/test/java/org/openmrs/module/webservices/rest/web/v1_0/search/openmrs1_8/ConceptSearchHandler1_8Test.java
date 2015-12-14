package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

public class ConceptSearchHandler1_8Test extends MainResourceControllerTest {

    private ConceptService service;

    @Before
    public void init() throws Exception {
        service = Context.getConceptService();
    }
    @Override
    public String getURI() {
        return "concept";
    }

    @Test
    public void getSearchConfig_shouldReturnConcepyBySourceAndCode() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("source", "SNOMED CT");
        req.addParameter("code", "7345693");
        SimpleObject result = deserialize(handle(req));
        List<Object> hits = (List<Object>) result.get("results");
        Assert.assertEquals(1, hits.size());
        Assert.assertEquals(service.getConcept(5497).getUuid(), PropertyUtils.getProperty(hits.get(0), "uuid"));
    }

    @Override
    public String getUuid() {
        return "a09ab2c5-878e-4905-b25d-5784167d0216";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
     */
    @Override
    public long getAllCount() {
        return 24;
    }

}

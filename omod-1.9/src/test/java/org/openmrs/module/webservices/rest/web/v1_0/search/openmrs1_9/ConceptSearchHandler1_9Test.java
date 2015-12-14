package org.openmrs.module.webservices.rest.web.v1_0.search.openmrs1_9;

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

public class ConceptSearchHandler1_9Test extends MainResourceControllerTest{

    private ConceptService service;

    @Before
    public void init() throws Exception {
        service = Context.getConceptService();
    }

    @Override
    public String getURI() {
        return "concept";
    }

    @Override
    public long getAllCount() {
        return 24;
    }

    @Test
    public void getSearchConfig_shouldReturnConcepyByReferenceTermUuid() throws Exception {
        MockHttpServletRequest req = request(RequestMethod.GET, getURI());
        req.addParameter("term", "SSTRM-WGT234");
        SimpleObject result = deserialize(handle(req));
        List<Object> hits = (List<Object>) result.get("results");
        Assert.assertEquals(1, hits.size());
        List<Concept> concepts = service.getAllConcepts();
        Assert.assertEquals(service.getConcept(5089).getUuid(), PropertyUtils.getProperty(hits.get(0), "uuid"));
    }

    @Override
    public String getUuid() {
        return "c607c80f-1ea9-4da3-bb88-6276ce8868dd";
    }
}


package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.CohortService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link CohortController}.
 */
public class CohortMemberControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private static final String cohortUuid = "05e08b3b-5690-41e1-b651-5391fd946c1a";
	
	private static final String patientUuid = "a7e04421-525f-442f-8138-05b619d16def";
	
	private static final String datasetFilename = "customTestDataset.xml";
	
	private CohortService service;
	
	private CohortMemberController controller;
	
	private WebRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() throws Exception {
		this.service = Context.getCohortService();
		this.controller = new CohortMemberController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
		executeDataSet(datasetFilename);
	}
	
	private void log(String label, Object object) {
		String toPrint;
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.getSerializationConfig().set(SerializationConfig.Feature.INDENT_OUTPUT, true);
			toPrint = mapper.writeValueAsString(object);
		}
		catch (Exception ex) {
			toPrint = "" + object;
		}
		if (label != null)
			toPrint = label + ": " + toPrint;
		System.out.println(toPrint);
	}
	
	@Test
	public void getCohortMemeber_shouldGetADefaultRepresentationOfACohortMemeber() throws Exception {
		Object result = controller.retrieve(cohortUuid, patientUuid, request);
		Assert.assertNotNull(result);
		log("Cohort member fetched (default)", result);
	}
	
	@Test
	public void getAllCohortMemebers_shouldGetAllCohortMemebers() throws Exception {
		int size = Context.getCohortService().getCohortByUuid(cohortUuid).getMemberIds().size();
		List<Object> result = controller.getAll(cohortUuid, request, response);
		Assert.assertNotNull(result);
		log("Cohort member fetched (default)", result);
		Assert.assertEquals(result.size(), size);
	}
	
	@Test
	public void addCohortMemeber_shouldAddCohortMemeber() throws Exception {
		int before = Context.getCohortService().getCohortByUuid(cohortUuid).getMemberIds().size();
		String json = "{ \"patientUuid\":\"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object result = controller.create(cohortUuid, post, request, response);
		log("Add patient to cohort : ", result);
		Assert.assertEquals(before + 1, Context.getCohortService().getCohortByUuid(cohortUuid).getMemberIds().size());
	}
	
	@Test
	public void removeCohortMemeber_shouldRemoveCohortMemeber() throws Exception {
		int before = Context.getCohortService().getCohortByUuid(cohortUuid).getMemberIds().size();
		Object result = controller.delete(cohortUuid, patientUuid, "because", request, response);
		log("Removed patient from cohort : ", result);
		Assert.assertEquals(before - 1, Context.getCohortService().getCohortByUuid(cohortUuid).getMemberIds().size());
	}
	
}

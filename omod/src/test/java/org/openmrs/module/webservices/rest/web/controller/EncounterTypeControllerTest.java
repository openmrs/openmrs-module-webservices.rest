package org.openmrs.module.webservices.rest.web.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.EncounterType;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * Tests functionality of {@link EncounterTypeController}.
 */
public class EncounterTypeControllerTest extends BaseModuleWebContextSensitiveTest {
	
	private EncounterService service;
	
	private EncounterTypeController controller;
	
	private WebRequest request;
	
	private HttpServletResponse response;
	
	@Before
	public void before() {
		this.service = Context.getEncounterService();
		this.controller = new EncounterTypeController();
		this.request = new ServletWebRequest(new MockHttpServletRequest());
		this.response = new MockHttpServletResponse();
	}
	
	@Test
	public void shouldGetAEncounterTypeByUuid() throws Exception {
		Object result = controller.retrieve("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Scheduled", PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldGetAEncounterTypeByName() throws Exception {
		Object result = controller.retrieve("Scheduled", request);
		Assert.assertNotNull(result);
		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals("Scheduled", PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllUnRetiredEncounterTypes() throws Exception {
		List<Object> result = controller.getAll(request, response);
		Assert.assertNotNull(result);
		Assert.assertEquals(2, result.size());
	}
	
	@Test
	public void shouldCreateAEncounterType() throws Exception {
		int originalCount = service.getAllEncounterTypes().size();
		String json = "{ \"name\":\"test encounterType\", \"description\":\"description\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		Object newEncounterType = controller.create(post, request, response);
		Assert.assertNotNull(PropertyUtils.getProperty(newEncounterType, "uuid"));
		Assert.assertEquals(originalCount + 1, service.getAllEncounterTypes().size());
	}
	
	@Test
	public void shouldEditAEncounterType() throws Exception {
		String json = "{ \"name\":\"new encounter type\", \"description\":\"new description\" }";
		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
		controller.update("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", post, request, response);
		EncounterType updated = service.getEncounterTypeByUuid("61ae96f4-6afe-4351-b6f8-cd4fc383cce1");
		Assert.assertNotNull(updated);
		Assert.assertEquals("new encounter type", updated.getName());
		Assert.assertEquals("new description", updated.getDescription());
	}
	
	@Test
	public void shouldRetireAEncounterType() throws Exception {
		String uuid = "61ae96f4-6afe-4351-b6f8-cd4fc383cce1";
		EncounterType encounterType = service.getEncounterTypeByUuid(uuid);
		Assert.assertFalse(encounterType.isRetired());
		controller.delete(uuid, "test reason", request, response);
		encounterType = service.getEncounterTypeByUuid(uuid);
		Assert.assertTrue(encounterType.isRetired());
		Assert.assertEquals("test reason", encounterType.getRetireReason());
	}
	
	@Test
	public void shouldPurgeAEncounterType() throws Exception {
		//All the encounterTypes in the test dataset are already in use, so we need to
		//create one that we can purge for testing purposes
		EncounterType encounterType = new EncounterType();
		encounterType.setName("new test encounterType");
		encounterType.setDescription("new descriptionpe");
		service.saveEncounterType(encounterType);
		Assert.assertNotNull(encounterType.getUuid());//should have been inserted for the test to be valid
		int originalCount = service.getAllEncounterTypes().size();
		String uuid = encounterType.getUuid();
		controller.purge(uuid, request, response);
		Assert.assertNull(service.getEncounterTypeByUuid(uuid));
		Assert.assertEquals(originalCount - 1, service.getAllEncounterTypes().size());
	}
	
	@Test
	public void shouldSearchAndReturnAListOfEncounterTypesMatchingTheQueryString() throws Exception {
		List<Object> hits = controller.search("Sch", request, response);
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals("61ae96f4-6afe-4351-b6f8-cd4fc383cce1", PropertyUtils.getProperty(hits.get(0), "uuid"));
		
	}
}

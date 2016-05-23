/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Program;
import org.openmrs.api.ProgramWorkflowService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * Tests functionality of Program CRUD by MainResourceController
 */
public class ProgramController1_9Test extends MainResourceControllerTest {

	private ProgramWorkflowService service;

	@Before
	public void init() {
		service = Context.getProgramWorkflowService();
	}

	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "program";
	}

	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllPrograms(true).size();
	}

	/**
	 * @see MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.PROGRAM_UUID;
	}

	public String getName() {
		return "MALARIA PROGRAM";
	}
	
	@Test
	public void shouldGetAProgramByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Program program = service.getProgramByUuid(getUuid());
		Assert.assertNotNull(result);
		Assert.assertEquals(program.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(program.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldGetAProgramByName() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getName());
		SimpleObject result = deserialize(handle(req));
		
		Program program = service.getProgramByName(getName());
		Assert.assertNotNull(result);
		Assert.assertEquals(program.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(program.getName(), PropertyUtils.getProperty(result, "name"));
	}
	
	@Test
	public void shouldListAllUnRetiredPrograms() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
	}
	
	@Test
	public void shouldCreateAProgram() throws Exception {
		long originalCount = getAllCount();
		
		SimpleObject program = new SimpleObject();
		program.add("name", "Program name");
		program.add("description", "Program description");
		program.add("concept", RestTestConstants1_8.CONCEPT_UUID);

		String json = new ObjectMapper().writeValueAsString(program);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newProgram = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newProgram, "uuid"));
		Assert.assertEquals(RestTestConstants1_8.CONCEPT_UUID, ((Map) PropertyUtils.getProperty(newProgram, "concept")).get("uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
	}
	
	@Test
	public void shouldEditAProgram() throws Exception {
		
		final String editedName = "Malaria Program Edited";
		String json = "{ \"name\":\"" + editedName + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		
		Program editedProgram = service.getProgramByUuid(getUuid());
		Assert.assertNotNull(editedProgram);
		Assert.assertEquals(editedName, editedProgram.getName());
	}

	@Test
	public void shouldRetireAProgram() throws Exception {
		
		Program program = service.getProgram(2);
		Assert.assertFalse(program.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + program.getUuid());
		req.addParameter("!purge", "");
		req.addParameter("reason", "some good reason");
		handle(req);
		
		Program retiredProgram = service.getProgram(2);
		Assert.assertTrue(retiredProgram.isRetired());
		Assert.assertEquals("some good reason", retiredProgram.getRetireReason());
	}
	
	@Test
	public void shouldPurgeARetiredProgram() throws Exception {
		
		Program program = service.getProgram(3);
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + program.getUuid());
		req.addParameter("purge", "");
		handle(req);
		
		Assert.assertNull(service.getProgram(3));
	}

	@Test
	public void shouldSearchAndReturnAListOfProgramsMatchingTheQueryString() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		req.addParameter("q", "mal");
		SimpleObject result = deserialize(handle(req));
		
		List<Object> hits = (List<Object>) result.get("results");
		Assert.assertEquals(1, hits.size());
		Assert.assertEquals(service.getProgram(3).getUuid(), PropertyUtils.getProperty(hits.get(0), "uuid"));
	}

}

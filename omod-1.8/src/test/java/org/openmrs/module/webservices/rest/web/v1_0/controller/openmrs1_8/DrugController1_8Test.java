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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Drug;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link DrugController}. This does not use @should
 * annotations because the controller inherits those methods from a subclass
 */
public class DrugController1_8Test extends MainResourceControllerTest {
	
	private ConceptService service;
	
	@Before
	public void init() throws Exception {
		service = Context.getConceptService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "drug";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return service.getAllDrugs(false).size();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.DRUG_UUID;
	}
	
	@Test
	public void shouldGetADrugByUuid() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + getUuid());
		SimpleObject result = deserialize(handle(req));
		
		Drug drug = service.getDrugByUuid(getUuid());
		Assert.assertNotNull(result);
		Assert.assertEquals(drug.getUuid(), PropertyUtils.getProperty(result, "uuid"));
		Assert.assertEquals(drug.getName(), PropertyUtils.getProperty(result, "name"));
		
	}
	
	@Test
	public void shouldListAllUnRetiredDrugs() throws Exception {
		
		MockHttpServletRequest req = request(RequestMethod.GET, getURI());
		SimpleObject result = deserialize(handle(req));
		
		Assert.assertNotNull(result);
		Assert.assertEquals(getAllCount(), Util.getResultsSize(result));
		
	}
	
	@Test
	public void shouldCreateADrug() throws Exception {
		
		long originalCount = getAllCount();
		
		SimpleObject drug = new SimpleObject();
		drug.add("name", "Drug name");
		drug.add("description", "Drug description");
		drug.add("combination", "false");
		drug.add("concept", service.getConcept(3).getUuid());
		
		String json = new ObjectMapper().writeValueAsString(drug);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newDrug = deserialize(handle(req));
		
		Assert.assertNotNull(PropertyUtils.getProperty(newDrug, "uuid"));
		Assert.assertEquals(originalCount + 1, getAllCount());
		
	}
	
	@Test
	public void shouldEditADrug() throws Exception {
		
		final String editedName = "Aspirin Edited";
		String json = "{ \"name\":\"" + editedName + "\" }";
		MockHttpServletRequest req = request(RequestMethod.POST, getURI() + "/" + getUuid());
		req.setContent(json.getBytes());
		handle(req);
		
		Drug editedDrug = service.getDrugByUuid(getUuid());
		Assert.assertNotNull(editedDrug);
		Assert.assertEquals(editedName, editedDrug.getName());
		
	}
	
	@Test
	public void shouldRetireADrug() throws Exception {
		
		Drug drug = service.getDrugByUuid(getUuid());
		Assert.assertFalse(drug.isRetired());
		
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + drug.getUuid());
		req.addParameter("!purge", "");
		req.addParameter("reason", "random reason");
		handle(req);
		
		Drug retiredDrug = service.getDrugByUuid(getUuid());
		Assert.assertTrue(retiredDrug.isRetired());
		Assert.assertEquals("random reason", retiredDrug.getRetireReason());
		
	}
	
	@Test
	public void shouldPurgeADrug() throws Exception {
		
		Drug drug = service.getDrug(11);
		MockHttpServletRequest req = request(RequestMethod.DELETE, getURI() + "/" + drug.getUuid());
		req.addParameter("purge", "");
		handle(req);
		
		Assert.assertNull(service.getDrug(11));
		
	}
	
	@Test
	public void shouldReturnTheAuditInfoForTheFullRepresentation() throws Exception {
		
		MockHttpServletRequest httpReq = request(RequestMethod.GET, getURI() + "/" + getUuid());
		httpReq.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
		SimpleObject result = deserialize(handle(httpReq));
		
		Assert.assertNotNull(result);
		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
		
	}
	
}

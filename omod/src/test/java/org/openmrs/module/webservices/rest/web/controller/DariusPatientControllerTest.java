package org.openmrs.module.webservices.rest.web.controller;


import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

public class DariusPatientControllerTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * @see DariusPatientController#getNames(Patient,WebRequest)
	 * @verifies return a list of names
	 */
	@Test
	public void getNames_shouldReturnAListOfNames() throws Exception {
		Object names = new DariusPatientController().getNames(Context.getPatientService().getPatient(2), new ServletWebRequest(new MockHttpServletRequest()));
		System.out.println(names);
		Assert.assertNotNull(names);
	}

	/**
     * @see DariusPatientController#addName(SimpleObject,Patient)
     * @verifies add a name
     */
    @Test
    public void addName_shouldAddAName() throws Exception {
    	Patient p = Context.getPatientService().getPatient(2);
    	int before = p.getNames().size();
    	SimpleObject post = new ObjectMapper().readValue("{\"givenName\":\"Darius\", \"familyName\":\"Programmer\"}", SimpleObject.class);
    	Object newName = new DariusPatientController().addName(post, p);
    	System.out.println(newName);
    	Assert.assertNotNull(newName);
    	Assert.assertEquals(before + 1, Context.getPatientService().getPatient(2).getNames().size());
    }
}
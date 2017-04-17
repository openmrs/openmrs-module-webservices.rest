/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_1;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.*;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.ObsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

public class ObsController2_1Test extends MainResourceControllerTest {
	
	private ConceptService conceptService;
	
	private AdministrationService adminService;
	
	private ObsService obsService;
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "obs";
	}
	
	@Override
	public long getAllCount() {
		return Context.getObsService().getObservationCount(null, true);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.OBS_UUID;
	}
	
	@Before
	public void before() throws Exception {
		this.conceptService = Context.getConceptService();
		this.adminService = Context.getAdministrationService();
		this.obsService = Context.getObsService();
	}
	
	@Test
	public void shouldPostValueInJsonAndFetchComplexObs() throws Exception {
		ConceptComplex conceptComplex = newConceptComplex();
		InputStream in = getClass().getClassLoader().getResourceAsStream("customTestDataset.xml");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		
		String value = DatatypeConverter.printBase64Binary(out.toByteArray());
		
		final String contentType = "text/xml";
		String json = "{\"concept\":\"" + conceptComplex.getUuid() + "\"," + "\"value\":\"" + "data:" + contentType
		        + ";base64," + value + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\","
		        + "\"obsDatetime\":\"2015-09-07T00:00:00.000+0530\"}";
		
		SimpleObject responsePost = deserialize(handle(newPostRequest(getURI(), json)));
		
		String obsUuid = responsePost.get("uuid").toString();
		
		MockHttpServletResponse rawResponse = handle(newGetRequest(getURI() + "/" + obsUuid + "/value"));
		// Check if data is same
		Assert.assertThat(out.toByteArray(), is(equalTo(rawResponse.getContentAsByteArray())));
		
		Obs obsComplex = obsService.getObsByUuid(obsUuid);
		
		// Check MIME type
		assertThat(obsComplex.getComplexData().getMimeType(), is(equalTo(contentType)));
	}
	
	@Test
	public void shouldSupportMimeTypesWhenPostingRawData() throws Exception {
		ConceptComplex conceptComplex = newConceptComplex();
		InputStream in = getClass().getClassLoader().getResourceAsStream("customTestDataset.xml");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		IOUtils.copy(in, out);
		
		String json = "{\"concept\":\"" + conceptComplex.getUuid()
		        + "\", \"person\":\"5946f880-b197-400b-9caa-a3c661d23041\","
		        + "\"obsDatetime\":\"2015-09-07T00:00:00.000+0530\"}";
		
		MockMultipartHttpServletRequest request = newUploadRequest(getURI());
		final String contentTypeKey = "Content-Type";
		final String contentType = "text/xml";
		request.addFile(new MockMultipartFile("file", "customTestDataset.xml", contentType, out.toByteArray()));
		request.addParameter("json", json);
		
		SimpleObject response = deserialize(handle(request));
		
		MockHttpServletResponse rawResponse = handle(newGetRequest(getURI() + "/" + response.get("uuid") + "/value"));
		
		assertThat(rawResponse.getHeader(contentTypeKey), is(equalTo(contentType)));
	}
	
	private ConceptComplex newConceptComplex() {
		setupBinaryDataHandler();
		ConceptComplex conceptComplex = new ConceptComplex();
		conceptComplex.setHandler("BinaryDataHandler");
		conceptComplex.addName(new ConceptName("Xml Test Data", Locale.ENGLISH));
		conceptComplex.setDatatype(conceptService.getConceptDatatypeByName("Complex"));
		conceptComplex.setConceptClass(conceptService.getConceptClassByName("Misc"));
		Set<ConceptDescription> set = new HashSet<ConceptDescription>();
		ConceptDescription conceptDescription = new ConceptDescription("Xml Test Data", Locale.ENGLISH);
		set.add(conceptDescription);
		conceptComplex.setDescriptions(set);
		conceptService.saveConcept(conceptComplex);
		return conceptComplex;
	}
	
	private void setupBinaryDataHandler() {
		adminService.saveGlobalProperty(new GlobalProperty("obs.complex_obs_dir", "complexObsDir"));
	}
	
}

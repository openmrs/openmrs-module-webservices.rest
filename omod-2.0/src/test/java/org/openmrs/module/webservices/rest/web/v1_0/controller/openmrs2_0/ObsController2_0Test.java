/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.ConceptComplex;
import org.openmrs.ConceptName;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.util.OpenmrsConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.equalTo;

public class ObsController2_0Test extends MainResourceControllerTest {
	
	@Autowired
	ConceptService conceptService;
	
	@Autowired
	AdministrationService adminService;
	
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
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test(expected = ResourceDoesNotSupportOperationException.class)
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Test
	public void shouldSupportMimeTypesWhenPostingBase64Binary() throws Exception {
		ConceptComplex conceptComplex = newConceptComplex();
		
		InputStream in = getClass().getClassLoader().getResourceAsStream("ComplexObsTestImage.png");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(ImageIO.read(in), "png", out);
		
		String value = DatatypeConverter.printBase64Binary(out.toByteArray());
		String contentType = "image/png";
		
		String json = "{\"concept\":\"" + conceptComplex.getUuid() + "\"," + "\"value\":\"" + "data:"
		        + contentType + ";base64," + value + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\","
		        + "\"obsDatetime\":\"2015-09-07T00:00:00.000+0530\"}";
		
		SimpleObject response = deserialize(handle(newPostRequest(getURI(), json)));
		
		MockHttpServletResponse rawResponse = handle(newGetRequest(getURI() + "/" + response.get("uuid") + "/value"));
		
		assertThat(contentType, is(equalTo(rawResponse.getContentType())));
		assertThat(out.toByteArray(), is(equalTo(rawResponse.getContentAsByteArray())));
	}
	
	private ConceptComplex newConceptComplex() {
		setupBinaryDataHandler();
		
		ConceptComplex conceptComplex = new ConceptComplex();
		conceptComplex.setHandler("ImageHandler");
		conceptComplex.addName(new ConceptName("Xml Test Data", Locale.ENGLISH));
		conceptComplex.setDatatype(conceptService.getConceptDatatypeByName("Complex"));
		conceptComplex.setConceptClass(conceptService.getConceptClassByName("Misc"));
		conceptService.saveConcept(conceptComplex);
		return conceptComplex;
	}
	
	private void setupBinaryDataHandler() {
		adminService.saveGlobalProperty(new GlobalProperty("obs.complex_obs_dir", "complexObsDir"));
	}
}

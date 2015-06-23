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
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.openmrs.activelist.Problem;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests CRUD operations for {@link Problem}s via web service calls
 */
public class SystemSettingController1_9Test extends MainResourceControllerTest {
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "systemsetting";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_9.GLOBAL_PROPERTY_UUID;
	}
        
        @Override
        public long getAllCount() {
            return 4;
        }
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
	 */
	@Override
	@Test
	public void shouldGetAll() throws Exception {
		super.shouldGetAll();
	}
	
	@Test
	public void shouldReturnValueAfterSavingSystemSetting() throws Exception {
		
		SimpleObject property = new SimpleObject();
		property.add("property", "a.property.test");
		property.add("value", "false");
		property.add("description", "Testing post operation of global property");
                property.add("datatypeClassname", "org.openmrs.customdatatype.datatype.BooleanDatatype");
		String json = new ObjectMapper().writeValueAsString(property);
		
		MockHttpServletRequest req = request(RequestMethod.POST, getURI());
		req.setContent(json.getBytes());
		
		SimpleObject newlyCreatedSetting = deserialize(handle(req));

		assertEquals("a.property.test", PropertyUtils.getProperty(newlyCreatedSetting, "property"));
                assertEquals(false, PropertyUtils.getProperty(newlyCreatedSetting, "value"));
	}
}

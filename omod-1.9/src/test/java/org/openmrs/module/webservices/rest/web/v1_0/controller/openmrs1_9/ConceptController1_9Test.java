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

import static org.hamcrest.Matchers.is;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_9;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Tests functionality of {@link ConceptController}. This does not use @should annotations because
 * the controller inherits those methods from a subclass
 */
public class ConceptController1_9Test extends MainResourceControllerTest {

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
	 */
	@Override
		public String getURI() {
			return "concept";
		}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
	 */
	@Override
		public String getUuid() {
			return RestTestConstants1_9.CONCEPT_UUID;
		}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
	 */
	@Override
		public long getAllCount() {
			return 24;
		}


	@Test
		public void shouldFindConceptsBySourceUuid() throws Exception {
			SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("term",
								"SSTRM-WGT234"))));
			List<Object> results = Util.getResultsList(response);

			Assert.assertEquals(results.size(), 1);
			Object next = results.iterator().next();
			Assert.assertThat((String) PropertyUtils.getProperty(next, "uuid"), is("c607c80f-1ea9-4da3-bb88-6276ce8868dd"));
		}

    @Test
    public void shouldFindNumericConceptsByQueryString() throws Exception {
        executeDataSet("numericConcept.xml");
        SimpleObject response = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "HEIGHT"), new Parameter("v", "full"))));
        List<Object> results = Util.getResultsList(response);

        Assert.assertEquals(results.size(), 1);
        Object next = results.iterator().next();
        Assert.assertThat((String) PropertyUtils.getProperty(next, "uuid"), is("568b58c8-e878-11e0-950d-00248140a5e3"));
    }
    
	@Test
	public void shouldFindConceptByReferenceTerm() throws Exception {
		executeDataSet("customConceptDataset1_9.xml");
		MockHttpServletRequest req = request(RequestMethod.GET, getURI() + "/" + "CIEL:WGT234");
		SimpleObject result = deserialize(handle(req));
		Assert.assertThat((String) PropertyUtils.getProperty(result, "uuid"), is("c607c80f-1ea9-4da3-bb88-6276ce8868dd"));
	}
	
}

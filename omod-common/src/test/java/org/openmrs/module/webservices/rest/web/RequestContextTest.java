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
package org.openmrs.module.webservices.rest.web;

import org.junit.Test;
import org.openmrs.api.APIException;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

public class RequestContextTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * @see RequestContext#setLimit(Integer)
	 * @verifies not accept a value less than one
	 */
	@Test(expected = APIException.class)
	public void setLimit_shouldNotAcceptAValueLessThanOne() throws Exception {
		new RequestContext().setLimit(0);
	}
	
	/**
	 * @see RequestContext#setLimit(Integer)
	 * @verifies not accept a null value
	 */
	@Test(expected = APIException.class)
	public void setLimit_shouldNotAcceptANullValue() throws Exception {
		new RequestContext().setLimit(null);
	}
	
}

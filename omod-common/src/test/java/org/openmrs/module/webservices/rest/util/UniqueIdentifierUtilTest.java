/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * The {@link UniqueIdentifierUtil} test.
 */
public class UniqueIdentifierUtilTest {

	/**
	 * Is valid uuid?
	 * @verifies uuid for requested resource
	 * @see org.openmrs.module.webservices.rest.util.UniqueIdentifierUtil#isValidUUID(String)
	 * <should>should return true when called with valid uuid.</should>
	 */
	@Test
	public void isValidUUID_shouldReturnTrueWhenCalledWithValidUUID(){
		assertTrue(UniqueIdentifierUtil.isValidUUID("9d027900-617b-4d4b-8b9b-5073859fceea"));
	}

	/**
	 * Is valid uuid?
	 * @verifies uuid for requested resource
	 * @see org.openmrs.module.webservices.rest.util.UniqueIdentifierUtil#isValidUUID(String)
	 * <should>should return false when called with invalid uuid.</should>
	 */
	@Test
	public void isValidUUID_shouldReturnFalseWhenCalledWithInvalidUUID(){
		assertFalse(UniqueIdentifierUtil.isValidUUID(null));
		assertFalse(UniqueIdentifierUtil.isValidUUID(""));
		assertFalse(UniqueIdentifierUtil.isValidUUID("test-ss-ss-ss-s"));
		assertFalse(UniqueIdentifierUtil.isValidUUID("009692ee-f9309-4a74-bbd0-63b8baa5a927"));
		assertFalse(UniqueIdentifierUtil.isValidUUID("1-1-1-1"));
	}
}

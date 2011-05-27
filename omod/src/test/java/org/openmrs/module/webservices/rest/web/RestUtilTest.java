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

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link RestUtil} class.
 */
public class RestUtilTest {
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return false if list is empty
	 */
	@Test
	public void ipMatches_shouldReturnFalseIfListIsEmpty() throws Exception {
		Assert.assertFalse(RestUtil.ipMatches("0.0.0.0", new ArrayList<String>()));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return false if there is no match
	 */
	@Test
	public void ipMatches_shouldReturnFalseIfThereIsNoMatch() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("0.0.0.0");
		candidateIps.add("1.1.1.1");
		
		Assert.assertFalse(RestUtil.ipMatches("2.2.2.2", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return true for exact match
	 */
	@Test
	public void ipMatches_shouldReturnTrueForExactMatch() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("0.0.0.0");
		candidateIps.add("1.1.1.1");
		
		Assert.assertTrue(RestUtil.ipMatches("1.1.1.1", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return true for match with asteriks
	 */
	@Test
	public void ipMatches_shouldReturnTrueForMatchWithAsteriks() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("0.0.0.0");
		candidateIps.add("1.1.*.1");
		
		Assert.assertTrue(RestUtil.ipMatches("1.1.1.1", candidateIps));
	}
	
}

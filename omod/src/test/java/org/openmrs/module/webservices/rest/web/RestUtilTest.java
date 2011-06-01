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
	 * @verifies return true if list is empty
	 */
	@Test
	public void ipMatches_shouldReturnTrueIfListIsEmpty() throws Exception {
		Assert.assertTrue(RestUtil.ipMatches("10.0.0.0", new ArrayList<String>()));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return false if there is no match
	 */
	@Test
	public void ipMatches_shouldReturnFalseIfThereIsNoMatch() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("10.0.0.0");
		candidateIps.add("10.0.0.1");
		
		Assert.assertFalse(RestUtil.ipMatches("10.0.0.2", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return true for exact match
	 */
	@Test
	public void ipMatches_shouldReturnTrueForExactMatch() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("10.0.0.0");
		candidateIps.add("10.0.0.1");
		
		Assert.assertTrue(RestUtil.ipMatches("10.0.0.1", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return true for match with submask
	 */
	@Test
	public void ipMatches_shouldReturnTrueForMatchWithSubmask() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("10.0.0.0/30");
		
		Assert.assertTrue(RestUtil.ipMatches("10.0.0.1", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return false if there is no match with submask
	 */
	@Test
	public void ipMatches_shouldReturnFalseIfThereIsNoMatchWithSubmask() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("10.0.0.0/30");
		
		Assert.assertFalse(RestUtil.ipMatches("10.0.0.4", candidateIps));
	}
	
	/**
	 * @see RestUtil#ipMatches(String,List)
	 * @verifies return true for exact ipv6 match
	 */
	@Test
	public void ipMatches_shouldReturnTrueForExactIpv6Match() throws Exception {
		List<String> candidateIps = new ArrayList<String>();
		candidateIps.add("fe80:0:0:0:202:b3ff:fe1e:8329");
		
		Assert.assertTrue(RestUtil.ipMatches("fe80::202:b3ff:fe1e:8329", candidateIps));
	}
	
}

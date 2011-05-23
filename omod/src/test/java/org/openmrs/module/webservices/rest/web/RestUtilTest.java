package org.openmrs.module.webservices.rest.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

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
		
		Assert.assertTrue(RestUtil.ipMatches("1.1.*.1", candidateIps));
	}
	
}

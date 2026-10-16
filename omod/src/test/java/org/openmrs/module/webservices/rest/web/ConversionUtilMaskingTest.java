/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.module.webservices.rest.web.ConversionUtil.MASKED_PROPERTY;
import static org.openmrs.module.webservices.rest.web.representation.Representation.DEFAULT;
import static org.openmrs.module.webservices.rest.web.representation.Representation.REF;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.Person;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.security.MaskingTestRule;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

/**
 * What a representation looks like when a {@link org.openmrs.security.DomainObjectAuthorizationRule}
 * refuses an object it contains.
 */
public class ConversionUtilMaskingTest extends BaseModuleWebContextSensitiveTest {
	
	private static final int DENIED_PATIENT_ID = 7;
	
	private static final int ALLOWED_PATIENT_ID = 8;
	
	@AfterEach
	public void disarmRule() {
		MaskingTestRule.deniedPersonId = null;
	}
	
	@Test
	public void convertToRepresentation_shouldMaskAnObjectARuleRefuses() throws Exception {
		Patient patient = Context.getPatientService().getPatient(DENIED_PATIENT_ID);
		MaskingTestRule.deniedPersonId = DENIED_PATIENT_ID;
		
		Object converted = ConversionUtil.convertToRepresentation(patient, DEFAULT);
		
		assertTrue(isMasked(converted), "a refused patient should come out masked, got " + converted);
		assertEquals(1, asMap(converted).size(), "a mask should carry nothing but the marker");
	}
	
	@Test
	public void convertToRepresentation_shouldNotMaskAnObjectNoRuleRefuses() throws Exception {
		Patient patient = Context.getPatientService().getPatient(ALLOWED_PATIENT_ID);
		MaskingTestRule.deniedPersonId = DENIED_PATIENT_ID;
		
		Object converted = ConversionUtil.convertToRepresentation(patient, DEFAULT);
		
		assertFalse(isMasked(converted));
		assertNotNull(asMap(converted).get("uuid"));
	}
	
	@Test
	public void convertToRepresentation_shouldMaskANestedObjectARuleRefuses() throws Exception {
		Obs obs = anObsOn(DENIED_PATIENT_ID);
		MaskingTestRule.deniedPersonId = DENIED_PATIENT_ID;
		
		Map<String, Object> converted = asMap(ConversionUtil.convertToRepresentation(obs, DEFAULT));
		
		assertNotNull(converted.get("uuid"), "the observation itself is not refused");
		assertTrue(isMasked(converted.get("person")), "the person it hangs off is, got " + converted.get("person"));
	}
	
	@Test
	public void convertToRepresentation_shouldKeepACollectionsSizeWhenAnElementIsMasked() throws Exception {
		List<Person> people = Arrays.asList(Context.getPersonService().getPerson(DENIED_PATIENT_ID),
		    Context.getPersonService().getPerson(ALLOWED_PATIENT_ID));
		MaskingTestRule.deniedPersonId = DENIED_PATIENT_ID;
		
		Object converted = ConversionUtil.convertToRepresentation(people, REF);
		
		List<?> representations = assertInstanceOf(List.class, converted);
		assertEquals(2, representations.size(), "a masked element is replaced, never dropped");
		assertTrue(isMasked(representations.get(0)));
		assertFalse(isMasked(representations.get(1)));
	}
	
	@Test
	public void convertToRepresentation_shouldDropRatherThanMaskWhenThePrivilegeIsMissingToo() throws Exception {
		Person person = Context.getPersonService().getPerson(DENIED_PATIENT_ID);
		MaskingTestRule.deniedPersonId = DENIED_PATIENT_ID;
		Context.logout();
		
		Object converted = ConversionUtil.convertToRepresentation(person, DEFAULT, new PrivilegedPersonResource());
		
		assertSame(ConversionUtil.PRIVILEGE_DENIED, converted,
		    "a missing privilege keeps dropping the property, as it did before masking existed");
	}
	
	private Obs anObsOn(int personId) {
		Person person = Context.getPersonService().getPerson(personId);
		List<Obs> obs = Context.getObsService().getObservationsByPerson(person);
		assertFalse(obs.isEmpty(), "the test dataset should hold an observation on person " + personId);
		return obs.get(0);
	}
	
	private boolean isMasked(Object representation) {
		return representation instanceof Map && Boolean.TRUE.equals(((Map<?, ?>) representation).get(MASKED_PROPERTY));
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> asMap(Object representation) {
		return assertInstanceOf(Map.class, representation);
	}
	
	/**
	 * Demands a privilege nobody holds, to pin down which of the two checks answers first.
	 */
	public static class PrivilegedPersonResource extends BaseDelegatingResource<Person> {
		
		@Override
		public String getRequiredGetPrivilege() {
			return "Test-Privilege-Nobody-Has-4b1e9c2a";
		}
		
		@Override
		public SimpleObject asRepresentation(Person delegate, Representation rep) {
			return new SimpleObject().add("uuid", delegate.getUuid());
		}
		
		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}
		
		@Override
		public Person getByUniqueId(String uniqueId) {
			return null;
		}
		
		@Override
		protected void delete(Person delegate, String reason, RequestContext context) {
		}
		
		@Override
		public void purge(Person delegate, RequestContext context) {
		}
		
		@Override
		public Person newDelegate() {
			return null;
		}
		
		@Override
		public Person save(Person delegate) {
			return delegate;
		}
		
		@Override
		public String getUri(Object delegate) {
			return "";
		}
	}
}

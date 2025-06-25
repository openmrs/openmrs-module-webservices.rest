/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.messagesource.MessageSourceService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8;

public class ConceptResource1_9Test extends BaseDelegatingResourceTest<ConceptResource1_9, Concept> {
	
	@Override
	public Concept newObject() {
		return Context.getConceptService().getConceptByUuid(getUuidProperty());
	}
	
	@Override
	public void validateDefaultRepresentation() throws Exception {
		super.validateDefaultRepresentation();
		assertPropPresent("name");
		assertPropPresent("datatype");
		assertPropPresent("conceptClass");
		assertPropPresent("set");
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("names");
		assertPropPresent("descriptions");
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("answers");
		assertPropPresent("setMembers");
	}
	
	@Override
	public void validateFullRepresentation() throws Exception {
		super.validateFullRepresentation();
		assertPropPresent("name");
		assertPropPresent("datatype");
		assertPropPresent("conceptClass");
		assertPropPresent("set");
		assertPropEquals("version", getObject().getVersion());
		assertPropEquals("retired", getObject().isRetired());
		assertPropPresent("names");
		assertPropPresent("descriptions");
		assertPropPresent("auditInfo");
		assertPropEquals("display", getDisplayProperty());
		assertPropPresent("answers");
		assertPropPresent("setMembers");
	}
	
	@Override
	public String getDisplayProperty() {
		return "YES";
	}
	
	@Override
	public String getUuidProperty() {
		return RestTestConstants1_8.CONCEPT_UUID;
	}
	
	@Test
	public void testSetNames() throws Exception {
		Concept instance = new Concept();
		List<ConceptName> otherNames = new ArrayList<ConceptName>();
		ConceptName otherName = new ConceptName();
		otherName.setLocale(Locale.ENGLISH);
		otherName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		otherName.setName("newName");
		otherName.setUuid("newUuid");
		otherNames.add(otherName);
		
		ConceptResource1_8.setNames(instance, otherNames);
		assertEquals(1, instance.getNames().size());
		assertTrue(instance.getNames().contains(otherName));
		
		ConceptResource1_8.setNames(instance, getMockNamesList());
		assertEquals(2, instance.getNames().size());
		assertFalse(instance.getNames().contains(otherName));
		
		otherNames.addAll(getMockNamesList());
		
		ConceptResource1_8.setNames(instance, otherNames);
		assertEquals(3, instance.getNames().size());
		assertTrue(instance.getNames().contains(otherName));
		
		ConceptResource1_8.setNames(instance, getMockNamesList());
		assertEquals(2, instance.getNames().size());
		assertFalse(instance.getNames().contains(otherName));
	}
	
	public List<ConceptName> getMockNamesList() {
		ConceptName oldName1 = new ConceptName();
		oldName1.setLocale(Locale.ENGLISH);
		oldName1.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		oldName1.setName("oldName1");
		oldName1.setUuid("uuid1");
		
		ConceptName oldName2 = new ConceptName();
		oldName2.setLocale(Locale.ENGLISH);
		oldName2.setConceptNameType(ConceptNameType.SHORT);
		oldName2.setName("oldName2");
		oldName2.setUuid("uuid2");
		
		List<ConceptName> oldNames = new ArrayList<ConceptName>();
		oldNames.add(oldName1);
		oldNames.add(oldName2);
		
		return oldNames;
	}
	
	@Test
	public void testGetNamedRepresentation() throws Exception {
		Concept object = getObject();
		object.addSetMember(object);
		try {
			SimpleObject rep = getResource().asRepresentation(object, new NamedRepresentation("fullchildreninternal"));
		}
		catch (ConversionException e) {
			Assert.assertFalse(e.getCause().getCause().getMessage().contains("Cycles in children are not supported."));
		}
	}

	@Test
	public void testGetDisplayName() throws Exception {
		Concept concept = new Concept();
		ConceptName fullySpecifiedName = new ConceptName("some name", new Locale("en", "US"));
		fullySpecifiedName.setConceptNameId(1);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		fullySpecifiedName.setLocalePreferred(false);
		concept.addName(fullySpecifiedName);

		ConceptResource1_9 resource = new ConceptResource1_9();
		String result = resource.getDisplayString(concept);

		Assert.assertEquals("some name", result);
	}

	@Test
	public void testGetDisplayNameForConceptWithNoName() throws Exception {
		Concept concept = new Concept(1);
		ConceptResource1_9 resource = new ConceptResource1_9();
		String result = resource.getDisplayString(concept);
		Assert.assertEquals("Concept #1", result);  // this will need to updated to "Concept #1" when we start building against more recent versions of OpenMRS Core
	}

	@Test
	public void testGetDisplayNameWithLocalizationOverride() throws Exception {

		String UUID = "f0c5b621-3e5e-4f0d-8b9e-1d3e2b2c1b2d";

		Concept concept = new Concept();
		concept.setUuid(UUID);
		MessageSourceService messageSourceService = mock(MessageSourceService.class);
		when(messageSourceService.getMessage("ui.i18n.Concept.name." + UUID)).thenReturn("Overridden by message source");
		ServiceContext.getInstance().setMessageSourceService(messageSourceService);

		ConceptName fullySpecifiedName = new ConceptName("some name", new Locale("en", "US"));
		fullySpecifiedName.setConceptNameId(1);
		fullySpecifiedName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
		fullySpecifiedName.setLocalePreferred(false);
		concept.addName(fullySpecifiedName);

		ConceptResource1_9 resource = new ConceptResource1_9();
		String result = resource.getDisplayString(concept);

		Assert.assertEquals("Overridden by message source", result);
	}
}

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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

public class ConceptResource1_8Test extends BaseDelegatingResourceTest<ConceptResource1_8, Concept> {

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
    	assertEquals(instance.getNames().size(), 1);
    	assertTrue(instance.getNames().contains(otherName));
    	
    	ConceptResource1_8.setNames(instance, getMockNamesList());
    	assertEquals(instance.getNames().size(), 2);
    	assertFalse(instance.getNames().contains(otherName));
    	
    	otherNames.addAll(getMockNamesList());
    	
    	ConceptResource1_8.setNames(instance, otherNames);
    	assertEquals(instance.getNames().size(), 3);
    	assertTrue(instance.getNames().contains(otherName));
    	
    	ConceptResource1_8.setNames(instance, getMockNamesList());
    	assertEquals(instance.getNames().size(), 2);
    	assertFalse(instance.getNames().contains(otherName));
	}
    @Test
    public void testCheckIfNamesContainNameByProperties() throws Exception { 
    	String duplicatedUuid = "newUuid";
    	List<ConceptName> someNames = new ArrayList<ConceptName>();
    	
    	ConceptName newName = new ConceptName();
    	newName.setLocale(Locale.ENGLISH);
    	newName.setConceptNameType(ConceptNameType.FULLY_SPECIFIED);
    	newName.setName("newName");
    	newName.setUuid(duplicatedUuid);
    	
    	//oldNames is empty
    	assertFalse(ConceptResource1_8.checkIfNamesContainNameByProperties(someNames, newName));
    	//assign some names, but different from newName
    	someNames = getMockNamesList();    	
    	assertFalse(ConceptResource1_8.checkIfNamesContainNameByProperties(someNames, newName));
    	
    	someNames.add(newName);
    	assertTrue(ConceptResource1_8.checkIfNamesContainNameByProperties(someNames, newName));
    	
    	ConceptName otherName = new ConceptName();
    	otherName.setUuid(duplicatedUuid);
    	assertTrue(ConceptResource1_8.checkIfNamesContainNameByProperties(someNames, otherName));
    }
    
    public List<ConceptName> getMockNamesList(){
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
        }catch (ConversionException e){
            Assert.assertFalse(e.getCause().getCause().getMessage().contains("Cycles in children are not supported."));
        }
    }
}

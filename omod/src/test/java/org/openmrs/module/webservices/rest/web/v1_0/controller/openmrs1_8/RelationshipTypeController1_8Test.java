/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.RelationshipType;
import org.openmrs.api.PersonService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Util;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;

import java.util.List;

/**
 * Contains tests for {@link RelationshipTypeController} CRUD operations
 */
public class RelationshipTypeController1_8Test extends MainResourceControllerTest {
	
	private PersonService service;
	
	/**
	 * @see MainResourceControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "relationshiptype";
	}
	
	/**
	 * @see MainResourceControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return RestTestConstants1_8.RELATIONSHIP_TYPE_UUID;
	}
	
	/**
	 * @see MainResourceControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		int count = 0;
		for (RelationshipType type : service.getAllRelationshipTypes()) {
			if (!type.isRetired()) {
				count++;
			}
		}
		return count;
	}
	
	@BeforeEach
	public void before() {
		this.service = Context.getPersonService();
	}
	
	@Test
	public void shouldGetARelationshipTypeByName() throws Exception {
		Object result = deserialize(handle(newGetRequest(getURI() + "/Doctor")));
		Assertions.assertNotNull(result);
		Assertions.assertEquals(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assertions.assertEquals("Relationship from a primary care provider to the patient",
		    PropertyUtils.getProperty(result, "description"));
	}
	
	@Test
	public void shouldCreateARelationshipType() throws Exception {
		int originalCount = service.getAllRelationshipTypes().size();
		String json = "{ \"aIsToB\":\"Teacher\",\"bIsToA\":\"Student\",\"weight\":\"0\",\"description\":\"test relation\"}";
		Object newRelationshipType = deserialize(handle(newPostRequest(getURI(), json)));
		Assertions.assertNotNull(PropertyUtils.getProperty(newRelationshipType, "uuid"));
		Assertions.assertEquals(originalCount + 1, service.getAllRelationshipTypes().size());
	}
	
	@Test
	public void shouldEditARelationshipType() throws Exception {
		String json = "{ \"aIsToB\":\"Teacher\",\"bIsToA\":\"Student\",\"weight\":\"0\",\"description\":\"new description\"}";
		handle(newPostRequest(getURI() + "/" + getUuid(), json));
		RelationshipType updated = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		Assertions.assertNotNull(updated);
		Assertions.assertEquals("new description", updated.getDescription());
	}
	
	@Test
	public void shouldRetireARelationshipType() throws Exception {
		RelationshipType relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		Assertions.assertFalse(relationshipType.isRetired());
		handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test reason")));
		relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		Assertions.assertTrue(relationshipType.isRetired());
		Assertions.assertEquals("test reason", relationshipType.getRetireReason());
	}
	
	@Test
	public void shouldUnRetireARelationshipType() throws Exception {
		RelationshipType relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		relationshipType.setRetired(true);
		relationshipType.setRetireReason("random reason");
		service.saveRelationshipType(relationshipType);
		relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		Assertions.assertTrue(relationshipType.isRetired());
		
		String json = "{\"deleted\": \"false\"}";
		SimpleObject response = deserialize(handle(newPostRequest(getURI() + "/" + getUuid(), json)));
		
		relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
		Assertions.assertFalse(relationshipType.isRetired());
		Assertions.assertEquals("false", PropertyUtils.getProperty(response, "retired").toString());
		
	}
	
	@Test
	public void shouldPurgeARelationshipType() throws Exception {
		String uuid = "2a5f4ff4-a179-4b8a-aa4c-40f71956eabc";
		Assertions.assertNotNull(service.getRelationshipTypeByUuid(uuid));
		int originalCount = service.getAllRelationshipTypes().size();
		handle(newDeleteRequest(getURI() + "/" + uuid, new Parameter("purge", "true")));
		Assertions.assertNull(service.getRelationshipTypeByUuid(uuid));
		Assertions.assertEquals(originalCount - 1, service.getAllRelationshipTypes().size());
	}
	
	@Test
	public void shouldSearchAndReturnAListOfRelationshipTypesMatchingTheQueryString() throws Exception {
		SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Doc"))));
		List<Object> hits = Util.getResultsList(result);
		Assertions.assertEquals(1, hits.size());
		Assertions.assertEquals(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID, PropertyUtils.getProperty(hits.get(0), "uuid"));
		
	}
	
}

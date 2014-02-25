/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "relationshiptype";
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants1_8.RELATIONSHIP_TYPE_UUID;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getAllCount()
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

    @Before
    public void before() {
        this.service = Context.getPersonService();
    }

    @Test
    public void shouldGetARelationshipTypeByName() throws Exception {
        Object result = deserialize(handle(newGetRequest(getURI() + "/Doctor")));
        Assert.assertNotNull(result);
        Assert.assertEquals(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID, PropertyUtils.getProperty(result, "uuid"));
        Assert.assertEquals("Relationship from a primary care provider to the patient", PropertyUtils.getProperty(result, "description"));
    }

    @Test
    public void shouldCreateARelationshipType() throws Exception {
        int originalCount = service.getAllRelationshipTypes().size();
        String json = "{ \"aIsToB\":\"Teacher\",\"bIsToA\":\"Student\",\"weight\":\"0\",\"description\":\"test relation\"}";
        Object newRelationshipType = deserialize(handle(newPostRequest(getURI(), json)));
        Assert.assertNotNull(PropertyUtils.getProperty(newRelationshipType, "uuid"));
        Assert.assertEquals(originalCount + 1, service.getAllRelationshipTypes().size());
    }


    @Test
    public void shouldEditARelationshipType() throws Exception {
        String json = "{ \"aIsToB\":\"Teacher\",\"bIsToA\":\"Student\",\"weight\":\"0\",\"description\":\"new description\"}";
        handle(newPostRequest(getURI() + "/" + getUuid(), json));
        RelationshipType updated = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
        Assert.assertNotNull(updated);
        Assert.assertEquals("new description", updated.getDescription());
    }


    @Test
    public void shouldRetireARelationshipType() throws Exception {
        RelationshipType relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
        Assert.assertFalse(relationshipType.isRetired());
        handle(newDeleteRequest(getURI() + "/" + getUuid(), new Parameter("reason", "test reason")));
        relationshipType = service.getRelationshipTypeByUuid(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID);
        Assert.assertTrue(relationshipType.isRetired());
        Assert.assertEquals("test reason", relationshipType.getRetireReason());
    }


    @Test
    public void shouldPurgeARelationshipType() throws Exception {
        String uuid = "2a5f4ff4-a179-4b8a-aa4c-40f71956eabc";
        Assert.assertNotNull(service.getRelationshipTypeByUuid(uuid));
        int originalCount = service.getAllRelationshipTypes().size();
        handle(newDeleteRequest(getURI() + "/" + uuid, new Parameter("purge", "")));
        Assert.assertNull(service.getRelationshipTypeByUuid(uuid));
        Assert.assertEquals(originalCount - 1, service.getAllRelationshipTypes().size());
    }


    @Test
    public void shouldSearchAndReturnAListOfRelationshipTypesMatchingTheQueryString() throws Exception {
        SimpleObject result = deserialize(handle(newGetRequest(getURI(), new Parameter("q", "Doc"))));
        List<Object> hits = Util.getResultsList(result);
        Assert.assertEquals(1, hits.size());
        Assert.assertEquals(RestTestConstants1_8.RELATIONSHIP_TYPE_UUID, PropertyUtils.getProperty(hits.get(0), "uuid"));

    }

}

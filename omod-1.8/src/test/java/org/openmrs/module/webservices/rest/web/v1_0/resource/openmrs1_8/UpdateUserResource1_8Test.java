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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.UserAndPassword1_8;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Integration tests for the framework that lets a resource handle an entire class hierarchy
 */
public class UpdateUserResource1_8Test extends BaseModuleWebContextSensitiveTest {

    private UserResource1_8 resource;

    @Before
    public void beforeEachTests() throws Exception {
        resource = (UserResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(UserAndPassword1_8.class);
    }

    @Test
    public void shouldUpdateUser() throws Exception {
        SimpleObject userSimpleObject = new SimpleObject();
        
        userSimpleObject.putAll(new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream("update_user.json"), HashMap.class));
        SimpleObject updated = (SimpleObject) resource.update("c98a1558-e131-11de-babe-001e378eb67e", userSimpleObject, new RequestContext());

        Map<String, String> userProperties = (Map<String, String>) updated.get("userProperties");
        Assert.assertEquals(2, userProperties.size());
        Assert.assertNotNull(userProperties.get("favouriteObsTemplates"));
        Assert.assertEquals("Gynaecology", userProperties.get("favouriteObsTemplates"));

        userSimpleObject.putAll(new ObjectMapper().readValue(getClass().getClassLoader().getResourceAsStream("update_user.json"), HashMap.class));
        SimpleObject updatedAgain = (SimpleObject) resource.update("c98a1558-e131-11de-babe-001e378eb67e", userSimpleObject, new RequestContext());
    }

}

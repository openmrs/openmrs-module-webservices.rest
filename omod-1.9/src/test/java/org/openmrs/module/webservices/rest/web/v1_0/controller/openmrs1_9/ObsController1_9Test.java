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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;

import org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8.ObsController1_8Test;

import java.util.HashMap;

/**
 * Created by tomasz on 27.04.15.
 */
public class ObsController1_9Test extends ObsController1_8Test {


    @Test
    public void getObs_shouldGetObsConceptByConceptMappings() throws Exception {
        String json = "{ \"value\":\""+10.0+"\", \"person\":\""
                + RestTestConstants1_8.PERSON_UUID +
                "\", \"concept\":\"SNOMED CT:2332523\", \"obsDatetime\":\"2013-12-09T00:00:00.000+0100\"}";


        Object newObs = deserialize(handle(newPostRequest(getURI(), json)));
        Assert.assertNotNull(PropertyUtils.getProperty(newObs, "concept"));
    }


}

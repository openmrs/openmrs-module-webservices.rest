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
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest;
import org.openmrs.util.OpenmrsConstants;

/**
 * Created by tomasz on 27.04.15.
 */
public class ObsController1_9Test extends MainResourceControllerTest {


    @Test
    public void getObs_shouldGetObsConceptByConceptMappings() throws Exception {
        String json = "{ \"value\":\""+10.0+"\", \"person\":\""
                + RestTestConstants1_8.PERSON_UUID +
                "\", \"concept\":\"SNOMED CT:2332523\", \"obsDatetime\":\"2013-12-09T00:00:00.000+0100\"}";


        Object newObs = deserialize(handle(newPostRequest(getURI(), json)));
        Assert.assertNotNull(PropertyUtils.getProperty(newObs, "concept"));
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getURI()
     */
    @Override
    public String getURI() {
        return "obs";
    }

    @Override
    public long getAllCount() {
        return Context.getObsService().getObservationCount(null,true);
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#getUuid()
     */
    @Override
    public String getUuid() {
        return RestTestConstants1_8.OBS_UUID;
    }

    /**
     * @see org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceControllerTest#shouldGetAll()
     */
    @Override
    @Test(expected = ResourceDoesNotSupportOperationException.class)
    public void shouldGetAll() throws Exception {
        super.shouldGetAll();
    }

    @Test
    public void shouldSubmitProperValueCodedWhenBooleanConceptUuidIsPassedAsValue() throws Exception {
        final String yesConceptUuid = "b055abd8-a420-4a11-8b98-02ee170a7b54";
        final String yesConceptId = "7";
        final String noConceptUuid = "934d8ef1-ea43-4f98-906e-dd03d5faaeb4";
        final String noConceptId = "8";

        AdministrationService as = Context.getAdministrationService();

        as.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_TRUE_CONCEPT, yesConceptId));
        as.saveGlobalProperty(new GlobalProperty(OpenmrsConstants.GLOBAL_PROPERTY_FALSE_CONCEPT, noConceptId));

        long before = getAllCount();

        String yesPayload = "{\"concept\":\"0dde1358-7fcf-4341-a330-f119241a46e8\"," +
                "\"value\":\"" + yesConceptUuid + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\"," +
                "\"obsDatetime\":\"2015-09-07T00:00:00.000+0530\"}";

        String noPayload = "{\"concept\":\"0dde1358-7fcf-4341-a330-f119241a46e8\"," +
                "\"value\":\"" + noConceptUuid + "\",\"person\":\"5946f880-b197-400b-9caa-a3c661d23041\"," +
                "\"obsDatetime\":\"2015-09-07T00:00:00.000+0530\"}";


        Object yesCreated = deserialize(handle(newPostRequest(getURI(), yesPayload)));
        Object yesValue = PropertyUtils.getProperty(yesCreated, "value");

        Object noCreated = deserialize(handle(newPostRequest(getURI(), noPayload)));
        Object noValue =  PropertyUtils.getProperty(noCreated, "value");

        Assert.assertEquals(before + 2, getAllCount());
        Assert.assertEquals(yesConceptUuid, PropertyUtils.getProperty(yesValue, "uuid"));
        Assert.assertEquals(noConceptUuid, PropertyUtils.getProperty(noValue, "uuid"));
    }
}


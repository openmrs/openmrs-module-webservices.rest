/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs2_0;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.v1_0.controller.RestControllerTestUtils;
import org.springframework.mock.web.MockHttpServletRequest;

public class NameTemplatesController2_0Test extends RestControllerTestUtils {
    
    private static final String SHORT_NAMETEMPLATE_RESOURCE = "nameTemplateShort.json";
    private static final String LONG_NAMETEMPLATE_RESOURCE = "nameTemplateLong.json";
    
    public String getURI() {
        return "nametemplates";
    }
    
    @Test
    public void shouldGetAll() throws Exception {
        MockHttpServletRequest req = newGetRequest(getURI());
        SimpleObject result = deserialize(handle(req));
        
        String shortNameTemplateJson;
        String longNameTemplateJson;
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(SHORT_NAMETEMPLATE_RESOURCE)) {
            shortNameTemplateJson = IOUtils.toString(inputStream, "UTF-8");
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(LONG_NAMETEMPLATE_RESOURCE)) {
            longNameTemplateJson = IOUtils.toString(inputStream, "UTF-8");
        }
        
        Assert.assertThat(result.containsKey("results"), is(true));
        Assert.assertThat(result.get("results"), is(not(empty())));
        Assert.assertThat(result.get("results"), hasItem(SimpleObject.parseJson(shortNameTemplateJson)));
        Assert.assertThat(result.get("results"), hasItem(SimpleObject.parseJson(longNameTemplateJson)));
    }
}

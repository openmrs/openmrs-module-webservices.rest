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
import org.openmrs.activelist.AllergyType;
import org.openmrs.module.webservices.rest.web.RestTestConstants1_8;
import org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8.AllergyController1_8Test;

public class AllergyController1_9Test extends AllergyController1_8Test {


    @Test
    public void getAllergen_shouldGetAllergenByConceptMappings() throws Exception {
        String json = "{\"person\":\""
                + RestTestConstants1_8.PERSON_UUID +
                "\", \"allergen\":\"SNOMED CT:2332523\", \"startDate\":\"2013-12-09\", \"allergyType\":\""
                + AllergyType.DRUG +"\"}";
Object newObs = deserialize(handle(newPostRequest(getURI(), json)));
        Assert.assertNotNull(PropertyUtils.getProperty(newObs, "allergen"));
    }


}

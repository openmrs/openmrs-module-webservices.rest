/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/rest/" + "v1" + "/order")
public class OrderValidateController {

    @RequestMapping(value = "/validate", method = RequestMethod.POST)
    @ResponseBody
    public Object validate(@RequestBody SimpleObject body) {

        SimpleObject response = new SimpleObject();

        try {
            DelegatingCrudResource resource = (DelegatingCrudResource) Context.getService(RestService.class).getResourceByName("v1/order");

            // THIS triggers the same validation as POST /order
            resource.convert(body);

            response.put("valid", true);
            response.put("errors", Collections.emptyList());
        } catch (Exception ex) {

            response.put("valid", false);

            List<SimpleObject> errorList = new ArrayList<>();
            SimpleObject e = new SimpleObject();
            e.put("message", ex.getMessage());
            errorList.add(e);

            response.put("errors", errorList);
        }

        return response;
    }
}
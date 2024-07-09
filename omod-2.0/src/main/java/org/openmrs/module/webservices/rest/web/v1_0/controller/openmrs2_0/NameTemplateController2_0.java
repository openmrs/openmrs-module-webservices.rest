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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.serialization.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/nametemplate")
public class NameTemplateController2_0  extends BaseRestController {

    private static final Logger log = LoggerFactory.getLogger(NameTemplateController2_0.class);

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Object get(WebRequest request) throws SerializationException {

        NameTemplate nameTemplate = NameSupport.getInstance().getDefaultLayoutTemplate();

        // Check global properties for defaults/overrides in the form of n=v,n1=v1, etc
        String customDefaults = Context.getAdministrationService().getGlobalProperty("layout.name.defaults");
        if (StringUtils.isNotEmpty(customDefaults)) {
            String[] tokens = customDefaults.split(",");
            Map<String, String> elementDefaults = nameTemplate.getElementDefaults();

            for (String token : tokens) {
                String[] pair = token.split("=");
                if (pair.length == 2) {
                    String name = pair[0];
                    String val = pair[1];

                    if (elementDefaults == null) {
                        elementDefaults = new HashMap<String, String>();
                    }
                    elementDefaults.put(name, val);
                } else {
                    log.debug("Found invalid token while parsing GlobalProperty name format defaults: {}", token);
                }
            }

            nameTemplate.setElementDefaults(elementDefaults);
        }

        MessageSource messageSource = Context.getMessageSourceService();
        List<List<Map<String, String>>> lines = nameTemplate.getLines();
        Map<String, String> nameMappings = nameTemplate.getNameMappings();
        for (List<Map<String, String>> line : lines) {
            for (Map<String, String> elements : line) {
                if (elements.containsKey("displayText")) {
                    String displayCode = elements.get("displayText");
                    if (StringUtils.isNotBlank(displayCode)) {
                        String displayText;
                        try {
                            displayText = messageSource.getMessage(displayCode, null, Context.getLocale());
                        }
                        catch (NoSuchMessageException e) {
                            displayText = displayCode;
                        }

                        elements.put("displayText", displayText);
                        String codeName = elements.get("codeName");
                        if (codeName != null && nameMappings.containsKey(codeName)) {
                            nameMappings.put(codeName, displayText);
                        }
                    }
                }
            }
        }

        SimpleObject nameTemplateSO = new SimpleObject();
        nameTemplateSO.put("displayName", nameTemplate.getDisplayName());
        nameTemplateSO.put("codeName", nameTemplate.getCodeName());
        nameTemplateSO.put("country", nameTemplate.getCountry());
        nameTemplateSO.put("lines", lines);
        nameTemplateSO.put("lineByLineFormat", nameTemplate.getLineByLineFormat());
        nameTemplateSO.put("nameMappings", nameMappings);
        nameTemplateSO.put("sizeMappings", nameTemplate.getSizeMappings());
        nameTemplateSO.put("elementDefaults", nameTemplate.getElementDefaults());
        nameTemplateSO.put("elementRegex", nameTemplate.getElementRegex());
        nameTemplateSO.put("elementRegexFormats", nameTemplate.getElementRegexFormats());
        nameTemplateSO.put("requiredElements", nameTemplate.getRequiredElements());

        return nameTemplateSO;
    }

}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.support.openmrs2_0;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.openmrs.GlobalProperty;
import org.openmrs.api.GlobalPropertyListener;
import org.openmrs.api.context.Context;
import org.openmrs.layout.LayoutSupport;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.template.openmrs2_0.NameTemplate2_0;
import org.openmrs.serialization.SerializationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameSupport2_0 extends LayoutSupport<NameTemplate2_0> implements GlobalPropertyListener {
    private static NameSupport2_0 singleton;
    private boolean initialized = false;
    static Logger log = LoggerFactory.getLogger(NameSupport2_0.class);

    private NameSupport2_0() {
        if (Objects.isNull(singleton)) {
            singleton = this;
        }
        log.debug("Setting singleton: {}", singleton);
    }

    public static NameSupport2_0 getInstance() {
        synchronized (NameSupport2_0.class) {
            if (Objects.isNull(singleton)) {
                singleton = new NameSupport2_0();
            }
        }

        singleton.init();
        return singleton;
    }

    private void init() {
        if (!this.initialized) {
            Context.getAdministrationService().addGlobalPropertyListener(singleton);
            String layoutTemplateXml = RestConstants.LAYOUT_NAME_FORMAT;
            this.setNameTemplate(layoutTemplateXml);
            List<String> specialTokens = new ArrayList<String>();
            specialTokens.add("familyName");
            specialTokens.add("middleName");
            specialTokens.add("givenName");
            this.setSpecialTokens(specialTokens);
            this.initialized = true;
        }
    }

    @Override
    public String getDefaultLayoutFormat() {
        return this.defaultLayoutFormat;
    }

    public void setNameTemplate(List<NameTemplate2_0> nameTemplates) {
        this.layoutTemplates = nameTemplates;
        this.setDefaultLayoutFormat(this.layoutTemplates.get(0).getCodeName());
    }

    public List<NameTemplate2_0> getNameTemplate() {
        if (this.layoutTemplates == null) {
            try {
                String xml = RestConstants.LAYOUT_NAME_FORMAT;
                this.setNameTemplate(xml);
            } catch (Exception ignored) {
            }
        }

        return this.layoutTemplates;
    }

    @Override
    public boolean supportsPropertyName(String s) {
        return RestConstants.LAYOUT_NAME_FORMAT.equals(s);
    }

    @Override
    public void globalPropertyChanged(GlobalProperty globalProperty) {
        if (RestConstants.LAYOUT_NAME_FORMAT.equals(globalProperty.getProperty())) {
            try {
                this.setNameTemplate(globalProperty.getPropertyValue());
            } catch (Exception var3) {
                log.error("Error in new xml global property value", var3);
                this.setNameTemplate((List)(new Vector()));
            }
        }
    }

    private void setNameTemplate(String xml) {
        NameTemplate2_0 nameTemplate;
        try {
            nameTemplate = Context.getSerializationService().getDefaultSerializer().deserialize(xml, NameTemplate2_0.class);
        } catch (SerializationException var4) {
            log.error("Error in deserializing name template", var4);
            nameTemplate = new NameTemplate2_0("Error while deserializing name layout template.");
        }

        List<NameTemplate2_0> list = new ArrayList<>();
        list.add(nameTemplate);
        this.setNameTemplate(list);
    }

    @Override
    public void globalPropertyDeleted(String s) {
        if (RestConstants.LAYOUT_NAME_FORMAT.equals(s)) {
            this.setNameTemplate((List)(new Vector()));
        }
    }
}

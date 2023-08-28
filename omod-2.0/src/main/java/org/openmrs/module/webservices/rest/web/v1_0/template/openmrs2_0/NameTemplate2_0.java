/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.template.openmrs2_0;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.layout.LayoutSupport;
import org.openmrs.layout.LayoutTemplate;
import org.openmrs.module.webservices.rest.web.v1_0.support.openmrs2_0.NameSupport2_0;

public class NameTemplate2_0 extends LayoutTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    public NameTemplate2_0(String string) {
        super(string);
    }
    @Override
    public String getLayoutToken() {
        return "IS_NAME_TOKEN";
    }

    @Override
    public String getNonLayoutToken() {
        return "IS_NOT_NAME_TOKEN";
    }

    @Override
    public LayoutSupport<?> getLayoutSupportInstance() {
        return NameSupport2_0.getInstance();
    }

    public String getCodeName() {
        if (StringUtils.isEmpty(this.codeName)) {
            this.codeName = "default";
        }
        return this.codeName;
    }
}

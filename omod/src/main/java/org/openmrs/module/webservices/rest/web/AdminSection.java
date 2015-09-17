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
package org.openmrs.module.webservices.rest.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.Extension;
import org.openmrs.module.web.extension.AdministrationSectionExt;
import org.openmrs.util.OpenmrsConstants;

/**
 * This class defines the links that will appear on the administration page under the
 * "basicmodule.title" heading. This extension is enabled by defining (uncommenting) it in the
 * /metadata/config.xml file.
 */
public class AdminSection extends AdministrationSectionExt {
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getMediaType()
	 */
	@Override
	public Extension.MEDIA_TYPE getMediaType() {
		return Extension.MEDIA_TYPE.html;
	}
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getTitle()
	 */
	@Override
	public String getTitle() {
		return RestConstants.MODULE_ID + ".title";
	}
	
	/**
	 * @see org.openmrs.module.web.extension.AdministrationSectionExt#getLinks()
	 */
	@Override
	public Map<String, String> getLinks() {
		
		Map<String, String> map = new LinkedHashMap<String, String>();
		
		if (Context.hasPrivilege(RestConstants.PRIV_MANAGE_RESTWS)) {
			map.put("module/webservices/rest/settings.form", RestConstants.MODULE_ID + ".manage.settings");
		}
		
		if (Context.hasPrivilege(RestConstants.PRIV_VIEW_RESTWS) || Context.hasPrivilege(RestConstants.PRIV_MANAGE_RESTWS)) {
			map.put("module/webservices/rest/test.htm", RestConstants.MODULE_ID + ".test");
			map.put("module/webservices/rest/swaggerDoc.htm", RestConstants.MODULE_ID + ".swaggerDocumentation");
			map.put("module/webservices/rest/help.form", RestConstants.MODULE_ID + ".help");
		}
		
		return map;
	}
	
}

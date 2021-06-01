/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.wrapper;

import org.openmrs.module.web.extension.AdministrationSectionExt;

import java.util.Map;

/**
 * REST wrapper for {@link AdministrationSectionExt}
 */
public class AdministrationSectionLinks2_0 {

	private String moduleId;

	private String title;

	private Map<String, String> links;

	public AdministrationSectionLinks2_0() {
	}

	public AdministrationSectionLinks2_0(String moduleId, String title, Map<String, String> links) {
		this.moduleId = moduleId;
		this.title = title;
		this.links = links;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map<String, String> getLinks() {
		return links;
	}

	public void setLinks(Map<String, String> links) {
		this.links = links;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.helper;

import org.openmrs.module.Module;

public class ModuleInstall {
	
	public ModuleInstall() {
	}
	
	public ModuleInstall(String moduleUuid, String installUri) {
		this.moduleUuid = moduleUuid;
		this.installUri = installUri;
	}
	
	private String moduleUuid;
	
	private String installUri;
	
	public void setModuleUuid(String moduleUuid) {
		this.moduleUuid = moduleUuid;
	}
	
	public String getModuleUuid() {
		return moduleUuid;
	}
	
	public void setInstallUri(String installUri) {
		this.installUri = installUri;
	}
	
	public String getInstallUri() {
		return installUri;
	}
	
}

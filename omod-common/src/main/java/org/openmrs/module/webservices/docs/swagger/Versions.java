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
package org.openmrs.module.webservices.docs.swagger;

import java.util.List;

public class Versions {
	
	private String platform;
	
	private List<ModuleVersion> modules;
	
	public Versions(String platform, List<ModuleVersion> modules) {
		this.platform = platform;
		this.modules = modules;
	}
	
	public String getPlatform() {
		return platform;
	}
	
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	
	public List<ModuleVersion> getModules() {
		return modules;
	}
	
	public void setModules(List<ModuleVersion> modules) {
		this.modules = modules;
	}
}

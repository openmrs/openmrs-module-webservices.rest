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

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

//List of Available Paths. 
public class Paths {
	
	private Map<String, Path> paths;
	
	public Paths() {
		
	}
	
	/**
	 * @return the paths
	 */
	@JsonAnyGetter
	public Map<String, Path> getPaths() {
		return paths;
	}
	
	/**
	 * @param paths the paths to set
	 */
	public void setPaths(Map<String, Path> paths) {
		this.paths = paths;
	}
	
}

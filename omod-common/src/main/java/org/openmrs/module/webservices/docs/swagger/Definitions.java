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

//List of Definitions
public class Definitions {
	
	private Map<String, Definition> definitions;
	
	public Definitions() {
		
	}
	
	/**
	 * @return the definitions
	 */
	@JsonAnyGetter
	public Map<String, Definition> getDefinitions() {
		return definitions;
	}
	
	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(Map<String, Definition> definitions) {
		this.definitions = definitions;
	}
	
}

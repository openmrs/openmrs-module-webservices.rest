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

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

// Describes properties of an object
public class Properties {
	
	private Map<String, DefinitionProperty> properties;
	
	public Properties() {
		properties = new HashMap<String, DefinitionProperty>();
	}
	
	/**
	 * @return the properties
	 */
	@JsonAnyGetter
	public Map<String, DefinitionProperty> getProperties() {
		return properties;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Map<String, DefinitionProperty> properties) {
		this.properties = properties;
	}
	
	public void addProperty(String propertyKey, DefinitionProperty property) {
		properties.put(propertyKey, property);
	}
	
}

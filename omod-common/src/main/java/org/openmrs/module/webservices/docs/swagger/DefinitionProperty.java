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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.ArrayList;
import java.util.List;

/*Defines the type of the property */
public class DefinitionProperty {
	
	private String type;
	
	@JsonProperty("enum")
	private List<String> enumeration;
	
	public DefinitionProperty() {
		
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@JsonGetter("enum")
	public List<String> getEnumeration() {
		return this.enumeration;
	}
	
	@JsonSetter("enum")
	public void setEnumeration(List<String> enumeration) {
		this.enumeration = enumeration;
	}
	
	public void addEnumerationItem(String enumerationItem) {
		if (enumeration == null)
			enumeration = new ArrayList<String>();
		
		if (!this.enumeration.contains(enumerationItem)) {
			this.enumeration.add(enumerationItem);
		}
	}
}

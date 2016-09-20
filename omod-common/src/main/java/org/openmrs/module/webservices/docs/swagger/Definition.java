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

import java.util.ArrayList;
import java.util.List;

/*An object that hold data types that can be consumed and produced by operations. These data types can be primitives, arrays or models.*/
public class Definition {
	
	private String type;
	
	private List<String> required;
	
	private Properties properties;
	
	private Xml xml;
	
	public Definition() {
		required = new ArrayList<String>();
		properties = new Properties();
	}
	
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return the required
	 */
	public List<String> getRequired() {
		return required;
	}
	
	/**
	 * @param required the required to set
	 */
	public void setRequired(List<String> required) {
		this.required = required;
	}
	
	public void addRequired(String property) {
		if (!required.contains(property)) {
			required.add(property);
		}
	}
	
	/**
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	
	public Xml getXml() {
		return xml;
	}
	
	public void setXml(Xml xml) {
		this.xml = xml;
	}
}

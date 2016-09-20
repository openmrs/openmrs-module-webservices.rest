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
package org.openmrs.module.webservices.docs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class stands for a resource representation.
 */
public class ResourceRepresentation {
	
	private String name;
	
	private Collection<String> properties = new ArrayList<String>();
	
	public ResourceRepresentation(String name, Collection<String> properties) {
		setName(name);
		setProperties(properties);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Collection<String> getProperties() {
		return properties;
	}
	
	public void setProperties(Collection<String> properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		/*String text = "h3. " + name;
		
		for (String property : properties) {
			text += System.getProperty("line.separator") + "* " + property;
		}*/
		
		String text = null;
		
		for (String property : properties) {
			if (text == null)
				text = "";
			else
				text += System.getProperty("line.separator");
			
			text += property;
		}
		
		return text;
	}
	
}

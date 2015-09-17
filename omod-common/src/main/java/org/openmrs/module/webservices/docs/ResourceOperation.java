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

/**
 * Represents an operation on a web service resource.
 */
public class ResourceOperation implements Comparable<ResourceOperation> {
	
	private String name;
	
	private String description;
	
	public ResourceOperation(String name, String description) {
		setName(name);
		setDescription(description);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "*" + name + "*: " + description;
	}
	
	@Override
	public int compareTo(ResourceOperation operation) {
		if (name.startsWith("GET")) {
			if (operation.getName().startsWith("GET"))
				return 0;
			else
				return -1;
		} else if (name.startsWith("POST")) {
			if (operation.getName().startsWith("POST"))
				return 0;
			else if (operation.getName().startsWith("GET"))
				return 1;
			else
				return -1;
		} else { //Must be a DELETE
			if (operation.getName().startsWith("DELETE"))
				return 0;
			else
				return 1;
		}
	}
}

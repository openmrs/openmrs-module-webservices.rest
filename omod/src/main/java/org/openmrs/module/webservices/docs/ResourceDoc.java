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

import java.util.ArrayList;
import java.util.List;

/**
 * Data structure containing documentation about a web service resource.
 */
public class ResourceDoc {
	
	private String name;
	
	private String url;
	
	private List<ResourceOperation> operations = new ArrayList<ResourceOperation>();
	
	private List<ResourceRepresentation> representations = new ArrayList<ResourceRepresentation>();
	
	public ResourceDoc(String name) {
		setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<ResourceOperation> getOperations() {
		return operations;
	}
	
	public void setOperations(List<ResourceOperation> operations) {
		this.operations = operations;
	}
	
	public List<ResourceRepresentation> getRepresentations() {
		return representations;
	}
	
	public void setRepresentations(List<ResourceRepresentation> representations) {
		this.representations = representations;
	}
	
	public void addRepresentation(ResourceRepresentation representation) {
		representations.add(representation);
	}
	
	@Override
	public String toString() {
		String text = name;
		
		text += System.getProperty("line.separator");
		text += url;
		
		if (operations.size() > 0) {
			text += System.getProperty("line.separator");
			text += "h2. Available HTTP Verbs";
			
			for (ResourceOperation operation : operations) {
				text += System.getProperty("line.separator");
				text += "* " + operation.toString();
			}
		}
		
		text += System.getProperty("line.separator");
		text += "h2. Representations";
		
		for (ResourceRepresentation representation : representations) {
			text += System.getProperty("line.separator");
			text += representation.toString();
		}
		
		return text;
	}
}

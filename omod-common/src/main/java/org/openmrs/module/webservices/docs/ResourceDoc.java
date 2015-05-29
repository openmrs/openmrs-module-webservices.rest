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
import java.util.Set;

/**
 * Data structure containing documentation about a web service resource.
 */
public class ResourceDoc implements Comparable<ResourceDoc> {
	
	private final static String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String name;
	
	//this is the value of the name attribute on the Resource annotation
	private String resourceName;
	
	////this is the value of the path attribute on the SubResource annotation
	private String subResourceName;
	
	private String subtypeHandlerForResourceName;
	
	private String url;
	
	private List<ResourceOperation> operations = new ArrayList<ResourceOperation>();
	
	private List<ResourceRepresentation> representations = new ArrayList<ResourceRepresentation>();
	
	private List<ResourceDoc> subResources = new ArrayList<ResourceDoc>();
	
	private List<ResourceDoc> subtypeHandlers = new ArrayList<ResourceDoc>();
	
	private String resourceVersion;
	
	private List<String> supportedOpenMRSVersion;
	
	public ResourceDoc(String name) {
		setName(name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}
	
	/**
	 * @param resourceName the resourceName to set
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	/**
	 * @return the subResourceName
	 */
	public String getSubResourceName() {
		return subResourceName;
	}
	
	/**
	 * @param subResourceName the subResourceName to set
	 */
	public void setSubResourceName(String subResourceName) {
		this.subResourceName = subResourceName;
	}
	
	public String getSubtypeHandlerForResourceName() {
		return subtypeHandlerForResourceName;
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
	
	public void addOperation(ResourceOperation operation) {
		operations.add(operation);
	}
	
	/**
	 * @return the subresources
	 */
	public List<ResourceDoc> getSubResources() {
		return subResources;
	}
	
	public void addSubResource(ResourceDoc resourceDoc) {
		subResources.add(resourceDoc);
	}
	
	public List<ResourceDoc> getSubtypeHandlers() {
		return subtypeHandlers;
	}
	
	public void addSubtypeHandler(ResourceDoc resourceDoc) {
		resourceDoc.subtypeHandlerForResourceName = this.name;
		subtypeHandlers.add(resourceDoc);
	}
	
	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		
		text.append("h1. " + name);
		
		if (subResourceName != null) {
			text.append(" (subclass)");
		}
		
		if (!operations.isEmpty()) {
			
			text.append(LINE_SEPARATOR);
			text.append("h3. URLs");
			
			text.append(LINE_SEPARATOR);
			text.append("|| url || description ||");
			
			for (ResourceOperation operation : operations) {
				text.append(LINE_SEPARATOR);
				text.append("| ");
				text.append(operation.getName());
				text.append(" | ");
				text.append(operation.getDescription());
				text.append(" | ");
			}
			
		}
		
		text.append(LINE_SEPARATOR);
		text.append("h3. Representations");
		
		text.append(LINE_SEPARATOR);
		text.append("|| ");
		
		for (ResourceRepresentation representation : representations) {
			text.append(representation.getName());
			text.append(" || ");
		}
		
		text.append(LINE_SEPARATOR);
		
		text.append("| ");
		for (ResourceRepresentation representation : representations) {
			text.append(representation.toString());
			text.append(" | ");
		}
		
		if (!subResources.isEmpty()) {
			for (ResourceDoc subresource : subResources) {
				text.append(LINE_SEPARATOR);
				text.append(subresource.toString());
			}
		}
		
		return text.toString();
	}
	
	@Override
	public int compareTo(ResourceDoc doc) {
		return name.compareTo(doc.getName());
	}
	
	public boolean isSubResource() {
		return subResourceName != null;
	}
	
	public boolean isSubtypeHandler() {
		return subtypeHandlerForResourceName != null;
	}
	
	/**
	 * @return the resourceVersion
	 */
	public String getResourceVersion() {
		return resourceVersion;
	}
	
	/**
	 * @param resourceVersion the resourceVersion to set
	 */
	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}
	
	/**
	 * @return the supportedOpenMRSVersion
	 */
	public List<String> getSupportedOpenMRSVersion() {
		return supportedOpenMRSVersion;
	}
	
	/**
	 * @param supportedOpenMRSVersion the supportedOpenMRSVersion to set
	 */
	public void setSupportedOpenMRSVersion(List<String> supportedOpenMRSVersion) {
		this.supportedOpenMRSVersion = supportedOpenMRSVersion;
	}
	
}

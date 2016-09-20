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

/* The class describes the RESTful API in accordance with the Swagger specification and is represented as JSON objects and conform to the JSON standards */
public class SwaggerSpecification {
	
	//Specifies the Swagger Specification version being used
	private String swagger = "2.0";
	
	// Provides metadata about the API
	private Info info;
	
	//The host (name or ip) serving the API
	private String host;
	
	//The base path on which the API is served
	private String basePath;
	
	//Allows adding meta data to a single tag that is used by the Operation Object.
	private List<Tag> tags;
	
	//The transfer protocol of the API
	private List<String> schemes;
	
	//A list of MIME types the APIs can consume
	private List<String> consumes;
	
	//A list of MIME types the APIs can produce
	private List<String> produces;
	
	//The available paths and operations for the API.
	private Paths paths;
	
	//The security definitions
	private SecurityDefinitions securityDefinitions;
	
	//An object to hold data types produced and consumed by operations.
	private Definitions definitions;
	
	public SwaggerSpecification() {
		
	}
	
	/**
	 * @return the info
	 */
	public Info getInfo() {
		return info;
	}
	
	/**
	 * @param info the info to set
	 */
	public void setInfo(Info info) {
		this.info = info;
	}
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}
	
	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}
	
	/**
	 * @return the schemes
	 */
	public List<String> getSchemes() {
		return schemes;
	}
	
	/**
	 * @param schemes the schemes to set
	 */
	public void setSchemes(List<String> schemes) {
		this.schemes = schemes;
	}
	
	/**
	 * @return the consumes
	 */
	public List<String> getConsumes() {
		return consumes;
	}
	
	/**
	 * @param consumes the consumes to set
	 */
	public void setConsumes(List<String> consumes) {
		this.consumes = consumes;
	}
	
	/**
	 * @return the produces
	 */
	public List<String> getProduces() {
		return produces;
	}
	
	/**
	 * @param produces the produces to set
	 */
	public void setProduces(List<String> produces) {
		this.produces = produces;
	}
	
	/**
	 * @return the paths
	 */
	public Paths getPaths() {
		return paths;
	}
	
	/**
	 * @param paths the paths to set
	 */
	public void setPaths(Paths paths) {
		this.paths = paths;
	}
	
	public SecurityDefinitions getSecurityDefinitions() {
		return securityDefinitions;
	}
	
	public void setSecurityDefinitions(SecurityDefinitions securityDefinitions) {
		this.securityDefinitions = securityDefinitions;
	}
	
	/**
	 * @return the definitions
	 */
	public Definitions getDefinitions() {
		return definitions;
	}
	
	/**
	 * @param definitions the definitions to set
	 */
	public void setDefinitions(Definitions definitions) {
		this.definitions = definitions;
	}
	
	/**
	 * @return the swagger
	 */
	public String getSwagger() {
		return swagger;
	}
	
	/**
	 * @return the tags
	 */
	public List<Tag> getTags() {
		return tags;
	}
	
	/**
	 * @param tags the tags to set
	 */
	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
}

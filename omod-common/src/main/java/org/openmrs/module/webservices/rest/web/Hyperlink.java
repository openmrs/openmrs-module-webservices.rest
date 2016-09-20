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
package org.openmrs.module.webservices.rest.web;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * A link to another resource
 */
@XStreamAlias("link")
public class Hyperlink {
	
	private String rel;
	
	private String uri;
	
	private transient String resourceAlias;
	
	public Hyperlink(String rel, String uri) {
		this.rel = rel;
		this.uri = uri;
	}
	
	/**
	 * @return the rel
	 */
	public String getRel() {
		return rel;
	}
	
	/**
	 * @param rel the rel to set
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}
	
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	@JsonIgnore
	public String getResourceAlias() {
		return resourceAlias;
	}
	
	@JsonIgnore
	public void setResourceAlias(String resourceAlias) {
		this.resourceAlias = resourceAlias;
	}
	
}

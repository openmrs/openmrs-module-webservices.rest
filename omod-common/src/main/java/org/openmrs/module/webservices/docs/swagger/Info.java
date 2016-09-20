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
import org.codehaus.jackson.annotate.JsonProperty;

/* The object provides metadata about the API.*/
public class Info {
	
	private String version;
	
	private String title;
	
	private String description;
	
	private Contact contact;
	
	private License license;
	
	//An object to hold data about platform and module versions
	@JsonProperty("x-versions")
	private Versions versions;
	
	public Info() {
		
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * @return the contact
	 */
	public Contact getContact() {
		return contact;
	}
	
	/**
	 * @param contact the contact to set
	 */
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	/**
	 * @return the license
	 */
	public License getLicense() {
		return license;
	}
	
	/**
	 * @param license the license to set
	 */
	public void setLicense(License license) {
		this.license = license;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@JsonGetter("x-versions")
	public Versions getVersions() {
		return versions;
	}
	
	public void setVersions(Versions versions) {
		this.versions = versions;
	}
}

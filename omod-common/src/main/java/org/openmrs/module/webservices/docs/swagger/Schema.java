package org.openmrs.module.webservices.docs.swagger;

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

import com.fasterxml.jackson.annotation.JsonProperty;

/*The Schema Object allows the definition of input and output data types. These types can be objects, but also primitives and arrays.*/
public class Schema {
	
	private String ref;
	
	public Schema() {
		
	}
	
	/**
	 * @return the ref
	 */
	@JsonProperty("$ref")
	public String getRef() {
		return ref;
	}
	
	/**
	 * @param ref the ref to set
	 */
	public void setRef(String ref) {
		this.ref = ref;
	}
	
}

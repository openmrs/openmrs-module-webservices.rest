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
package org.openmrs.module.webservices.rest.web.resource.api;

/**
 * Marker interface for resources
 */
public interface Resource {
	
	/**
	 * Gets the URI of the given instance of this resource. (If instance is null, this should return
	 * the base URI for creating and searching the resource.)
	 * 
	 * @param instance desc needed
	 * @return desc needed
	 */
	String getUri(Object instance);
	
}

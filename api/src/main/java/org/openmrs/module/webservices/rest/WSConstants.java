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
package org.openmrs.module.webservices.rest;

/**
 * Constants used by the Rest Web Services
 * 
 */
public class WSConstants {

	/**
	 * The number of results to limit lists of objects to if an admin has not
	 * defined a global property
	 * 
	 * @see #MAX_RESULTS_GLOBAL_PROPERTY_NAME
	 */
	public static Integer MAX_RESULTS_DEFAULT = 50;

	/**
	 * The key of the global property that an admin can set if they want to
	 * restrict lists to larger or smaller numbers than the default
	 * 
	 * @see #MAX_RESULTS_DEFAULT
	 */
	public static String MAX_RESULTS_GLOBAL_PROPERTY_NAME = "webservices.rest.maxresults";

	/**
	 * String that goes before every request. Its in a constant just in case we
	 * have to change it at some point for some strange reason
	 */
	public static String URL_PREFIX = "/ws/rest/";
	
	// the properties returned on an resource if no special rep is requested
	public static String REPRESENTATION_DEFAULT = "default";
	
	// more properties than default, but not all of them
	public static String REPRESENTATION_PARTIAL = "partial";
	
	// all properties on the resource are returned
	public static String REPRESENTATION_FULL = "full";
	public static String REPRESENTATION_CUSTOM_PREFIX = "custom:";
	
}

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

import java.util.HashSet;
import java.util.Set;

/**
 * Constants used by the Rest Web Services
 */
public class RestConstants {
	
	public static final String PRIV_MANAGE_RESTWS = "Manage RESTWS";
	
	public static final String PRIV_VIEW_RESTWS = "View RESTWS";
	
	/**
	 * The number of results to limit lists of objects to, if an admin has not defined a global
	 * property
	 * 
	 * @see #MAX_RESULTS_DEFAULT_GLOBAL_PROPERTY_NAME
	 */
	public static Integer MAX_RESULTS_DEFAULT = 50;
	
	/**
	 * The absolute number of results to limit lists of objects to, even if the call requests a
	 * larger list.
	 * 
	 * @see #MAX_RESULTS_ABSOLUTE_GLOBAL_PROPERTY_NAME
	 */
	public static Integer MAX_RESULTS_ABSOLUTE = 100;
	
	//module id or name
	public static final String MODULE_ID = "webservices.rest";
	
	/**
	 * The key of the global property that an admin can set if they want to restrict lists to larger
	 * or smaller numbers than the default
	 * 
	 * @see #MAX_RESULTS_DEFAULT
	 */
	public static String MAX_RESULTS_DEFAULT_GLOBAL_PROPERTY_NAME = MODULE_ID + ".maxResultsDefault";
	
	/**
	 * The key of the global property that an admin can set if they want an absolute limit to the
	 * maximum lists that can be returned in a webservice call
	 * 
	 * @see #MAX_RESULTS_ABSOLUTE
	 */
	public static String MAX_RESULTS_ABSOLUTE_GLOBAL_PROPERTY_NAME = MODULE_ID + ".maxResultsAbsolute";
	
	/**
	 * The key of the global property that an admin can set to restrict ws users based on a range of
	 * IPs. Should be a comma separated list of IP addresses. "*" in any part of it denotes a
	 * wildcard match.
	 */
	public static String ALLOWED_IPS_GLOBAL_PROPERTY_NAME = MODULE_ID + ".allowedips";
	
	/**
	 * The version number for the first rest web services representations
	 */
	public static final String VERSION_1 = "v1";
	
	/**
	 * The version number for the second rest web services representations
	 */
	public static final String VERSION_2 = "v2";
	
	/**
	 * String that goes before every request. Its in a constant just in case we have to change it at
	 * some point for some strange reason
	 */
	public static String URI_PREFIX;
	
	static {
		RestUtil.setUriPrefix();
	}
	
	/**
	 * An optional request parameter used by methods that return lists of patients to cut down on
	 * the number of potential results
	 */
	public static String REQUEST_PROPERTY_FOR_LIMIT = "limit";
	
	/**
	 * An optional request parameter used by methods that return lists of patients to determine how
	 * far into a list to start returning results.
	 */
	public static String REQUEST_PROPERTY_FOR_START_INDEX = "startIndex";
	
	/**
	 * An optional request parameter used by rest methods. Will change the properties on the
	 * results. Default is "default"
	 * 
	 * @see RequestContext#getRepresentation()
	 */
	public static String REQUEST_PROPERTY_FOR_REPRESENTATION = "v";
	
	/**
	 * An optional request parameter usable with resources that represent class hierarchies.
	 * Indicates that you only want results from a specific subclass
	 */
	public static final String REQUEST_PROPERTY_FOR_TYPE = "t";
	
	/**
	 * An optional request parameter used by methods that return a list of objects to determine
	 * whether voided (for data) or retired (for metadata) objects should be included in the list.
	 */
	public static final String REQUEST_PROPERTY_FOR_INCLUDE_ALL = "includeAll";
	
	/**
	 * An optional request parameter for the jsessionid
	 */
	public static final String REQUEST_PROPERTY_FOR_JSESSIONID = "jsessionid";
	
	/**
	 * An optional request parameter for a search id if the given search is ambiguous.
	 */
	public static final String REQUEST_PROPERTY_FOR_SEARCH_ID = "s";
	
	/**
	 * Used in object representations to indicate which specific type an instance belongs to for a
	 * resource that represents a full class hierarchy
	 */
	public static final String PROPERTY_FOR_TYPE = "type";
	
	// a ref is just a uuid/uri/display value
	public static String REPRESENTATION_REF = "ref";
	
	// the properties returned on an resource if no special rep is requested
	public static String REPRESENTATION_DEFAULT = "default";
	
	// all properties on the resource are returned
	public static String REPRESENTATION_FULL = "full";
	
	public static String REPRESENTATION_CUSTOM_PREFIX = "custom:";
	
	// The URI prefix through which clients consuming web services will connect
	// to the web application
	public static final String URI_PREFIX_GLOBAL_PROPERTY_NAME = MODULE_ID + ".uriPrefix";
	
	// The dafault value for URI prefix
	public static final String URI_PREFIX_GP_DEFAULT_VALUE = "NEED-TO-CONFIGURE";
	
	//The suffix to be used for all messages codes for logic names of global properties 
	//e.g "webservices.rest.fooBar.label for the 'webservices.rest.fooBar' global property
	public static final String GLOBAL_PROPERTY_LOGICAL_NAME_MESSAGE_CODE_SUFFIX = ".label";
	
	/**
	 * A required parameter of a resource indicating its version. It is automatically added to all
	 * representations.
	 */
	public static final String PROPERTY_FOR_RESOURCE_VERSION = "resourceVersion";
	
	/**
	 * A default value for the resource version parameter.
	 */
	public static final String PROPERTY_FOR_RESOURCE_VERSION_DEFAULT_VALUE = "1.8";
	
	/**
	 * The uuid property.
	 */
	public static final String PROPERTY_UUID = "uuid";
	
	/**
	 * A set of special request parameter names
	 */
	public static final Set<String> SPECIAL_REQUEST_PARAMETERS;
	
	static {
		SPECIAL_REQUEST_PARAMETERS = new HashSet<String>();
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_INCLUDE_ALL);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_LIMIT);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_REPRESENTATION);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_START_INDEX);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_TYPE);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_JSESSIONID);
		SPECIAL_REQUEST_PARAMETERS.add(REQUEST_PROPERTY_FOR_SEARCH_ID);
	}
	
}

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
 * Parses java source files and processes JavaDoc tags we use for web service documentation.
 */
public class JavadocParser {
	
	private static final String TAG_GET_DOCUMENTATION = "@getDocumentation";
	
	private static final String TAG_POST_DOCUMENTATION = "@postDocumentation";
	
	private static final String TAG_PUT_DOCUMENTATION = "@putDocumentation";
	
	private static final String TAG_DELETE_DOCUMENTATION = "@deleteDocumentation";
	
	/**
	 * Parses contents of a java source file and gets contents of the JavaDoc tags we use for
	 * documentation of web service operations available for a given resource.
	 * 
	 * @param source the java source code text.
	 * @return a list of resource operations.
	 */
	public static List<ResourceOperation> parse(String source) {
		
		// * @getDocumentation the get documentation text.
		// * @postDocumentation the post documentation text.
		// * @putDocumentation the put documentation text.
		// * @deleteDocumentation the delete documentation text.
		
		List<ResourceOperation> operations = new ArrayList<ResourceOperation>();
		
		int index = source.indexOf(TAG_GET_DOCUMENTATION);
		if (index > -1) {
			operations.add(new ResourceOperation("GET", getTagText(source, TAG_GET_DOCUMENTATION, index)));
		}
		
		index = source.indexOf(TAG_POST_DOCUMENTATION);
		if (index > -1) {
			operations.add(new ResourceOperation("POST", getTagText(source, TAG_POST_DOCUMENTATION, index)));
		}
		
		index = source.indexOf(TAG_PUT_DOCUMENTATION);
		if (index > -1) {
			operations.add(new ResourceOperation("PUT", getTagText(source, TAG_PUT_DOCUMENTATION, index)));
		}
		
		index = source.indexOf(TAG_DELETE_DOCUMENTATION);
		if (index > -1) {
			operations.add(new ResourceOperation("DELETE", getTagText(source, TAG_DELETE_DOCUMENTATION, index)));
		}
		
		return operations;
	}
	
	/**
	 * Gets text of a JavaDoc tag in a java source text.
	 * 
	 * @param source the java source text.
	 * @param tag the tag
	 * @param index the position at which the tag starts.
	 * @return the tag text.
	 */
	private static String getTagText(String source, String tag, int index) {
		
		//Assuming the * character is not used in the tag text, and so we use it to mark the end of tag text.
		return source.substring(index + TAG_POST_DOCUMENTATION.length(), source.indexOf('*', index)).trim();
	}
}

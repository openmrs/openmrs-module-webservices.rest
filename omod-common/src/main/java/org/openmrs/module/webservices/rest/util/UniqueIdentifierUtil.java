/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.util;

import java.util.regex.Pattern;

/**
 * The Universally Unique Identifier utility class. This class checks for a UUID's validity using a
 * regular expression. because Java UUID class doesn't provide any function to validate whether a
 * string is a valid UUID or not. This is particularly useful because of the need to Refactor
 * TaskAction Resource to take in a Task Definition uuid in addition to name. Requests for resources
 * must be made with valid UUIDs
 */
public class UniqueIdentifierUtil {
	
	private final static Pattern UUID_REGEX_PATTERN = Pattern
	        .compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
	
	/**
	 * Is valid uuid?
	 * 
	 * @param str the str to validate
	 * @return boolean
	 */
	public static boolean isValidUUID(String str) {
		if (str == null) {
			return false;
		}
		return UUID_REGEX_PATTERN.matcher(str).matches();
	}
}

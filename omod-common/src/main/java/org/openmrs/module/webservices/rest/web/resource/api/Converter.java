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

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Can convert from String -> T. Can convert from T -> json-friendly version of a given
 * Representation
 */
public interface Converter<T> {
	
	/**
	 * @param type user-friendly type name, if relevant for this converter (@see
	 *            DelegatingSubclassHandler)
	 * @return a new instance of the given type
	 */
	T newInstance(String type);
	
	/**
	 * @param string
	 * @return the result of converting the String input to a T
	 */
	T getByUniqueId(String string);
	
	/**
	 * @param instance
	 * @param rep
	 * @return a convertible-to-json object for instance in the given representation
	 * @throws Exception
	 */
	SimpleObject asRepresentation(T instance, Representation rep) throws ConversionException;
	
	/**
	 * @param instance
	 * @param propertyName
	 * @return
	 * @throws ConversionException
	 */
	Object getProperty(T instance, String propertyName) throws ConversionException;
	
	/**
	 * @param instance
	 * @param propertyName
	 * @param value
	 * @throws ConversionException
	 */
	void setProperty(Object instance, String propertyName, Object value) throws ConversionException;
	
}

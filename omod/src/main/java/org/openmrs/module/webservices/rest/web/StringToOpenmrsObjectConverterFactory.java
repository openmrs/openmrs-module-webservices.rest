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

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.api.RestService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Converts string uuids to full objects
 */
public class StringToOpenmrsObjectConverterFactory implements
		ConverterFactory<String, OpenmrsObject> {

	public <T extends OpenmrsObject> Converter<String, T> getConverter(
			Class<T> targetType) {
		return new StringToOpenmrsObjectConverter<T>(targetType);
	}

	/**
	 * Magical class that looks up a specific OpenmrsObject class by uuid
	 * 
	 * @param <T>
	 */
	private final class StringToOpenmrsObjectConverter<T extends OpenmrsObject>
			implements Converter<String, T> {

		private Class<T> openmrsObjectType;

		// constructor so we can know the type
		public StringToOpenmrsObjectConverter(Class<T> OpenmrsObjectType) {
			this.openmrsObjectType = OpenmrsObjectType;
		}

		// do the magic
		public T convert(String uuid) {
			RestService service = Context.getService(RestService.class);
			return (T) service.getOpenmrsObjectByUuid(openmrsObjectType, uuid);
		}
	}
}
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
package org.openmrs.module.webservices.rest.web.api;

import java.io.Serializable;
import java.util.List;

import org.openmrs.ConceptMap;

/**
 * It is provided as a workaround for missing API methods to fetch {@link ConceptMap}, etc.
 */
public interface RestHelperService {
	
	<T> T getObjectByUuid(Class<? extends T> type, String uuid);
	
	<T> T getObjectById(Class<? extends T> type, Serializable id);
	
	<T> List<T> getObjectsByFields(Class<? extends T> type, Field... fields);
	
	public static class Field {
		
		private final String name;
		
		private final Object value;
		
		public Field(String name, Object value) {
			this.name = name;
			this.value = value;
		}
		
		public String getName() {
			return name;
		}
		
		public Object getValue() {
			return value;
		}
	}
	
}

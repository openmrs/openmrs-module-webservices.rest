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
package org.openmrs.module.webservices.rest.web.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RuntimeWrappedException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;

/**
 * You can use this to override the property editors in the OpenMRS core, that are based off of
 * primary keys instead of UUIDs.
 */
public class UuidEditor extends PropertyEditorSupport {
	
	Class<? extends OpenmrsService> serviceClass;
	
	Method method;
	
	public UuidEditor(Class<? extends OpenmrsService> serviceClass, String methodName) throws SecurityException,
	        NoSuchMethodException {
		this.serviceClass = serviceClass;
		OpenmrsService service = Context.getService(serviceClass);
		method = service.getClass().getMethod(methodName, String.class);
	}
	
	@Override
	public void setAsText(String uuid) throws IllegalArgumentException {
		OpenmrsService service = Context.getService(serviceClass);
		try {
			Object val = method.invoke(service, uuid);
			if (val == null)
				throw new RuntimeWrappedException(new ObjectNotFoundException());
			setValue(val);
		}
		catch (IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
		catch (InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public String getAsText() {
		try {
			return (String) PropertyUtils.getProperty(getValue(), "uuid");
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}

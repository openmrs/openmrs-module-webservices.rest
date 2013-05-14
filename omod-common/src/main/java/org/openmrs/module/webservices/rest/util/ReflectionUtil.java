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
package org.openmrs.module.webservices.rest.util;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Utility methods for reflection and introspection
 */
public class ReflectionUtil {
	
	/**
	 * If clazz implements genericInterface<T, U, ...>, this method returns the parameterized type with
	 * the given index from that interface
	 */
	@SuppressWarnings("rawtypes")
	public static Class getParameterizedTypeFromInterface(Class<?> clazz, Class<?> genericInterface, int index) {
		for (Type t : clazz.getGenericInterfaces()) {
			if (t instanceof ParameterizedType && ((Class) ((ParameterizedType) t).getRawType()).equals(genericInterface)) {
				return (Class) ((ParameterizedType) t).getActualTypeArguments()[index];
			}
		}
		return null;
	}
	
	/**
	 *
	 * @param name the full method name to look for
	 * @return the java Method object if found. (does not return null)
	 * @throws RuntimeException if not method found by the given name in the current class
	 * @return
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		Method ret = ReflectionUtils.findMethod(clazz, name, (Class<?>[]) null);
		if (ret == null)
			throw new RuntimeException("No suitable method \"" + name + "\" in " + clazz);
		return ret;
	}
}

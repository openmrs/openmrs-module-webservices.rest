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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingPropertyAccessor;
import org.springframework.util.ReflectionUtils;

/**
 * Utility methods for reflection and introspection
 */
public class ReflectionUtil {
	
	private static ConcurrentMap<String, Method> setterMethodCache;
	
	private static ConcurrentMap<String, Method> getterMethodCache;
	
	private static Method nullMethod;
	
	static {
		setterMethodCache = new ConcurrentHashMap<String, Method>();
		getterMethodCache = new ConcurrentHashMap<String, Method>();
		
		// Just get a method from this class to use as the token null method
		nullMethod = ReflectionUtil.class.getDeclaredMethods()[0];
	}
	
	public static void clearCaches() {
		setterMethodCache = new ConcurrentHashMap<String, Method>();
		getterMethodCache = new ConcurrentHashMap<String, Method>();
	}
	
	/**
	 * If clazz implements genericInterface - T, U, ... -, this method returns the parameterized
	 * type with the given index from that interface. This method will recursively look at
	 * superclasses until it finds one implementing the requested interface
	 * 
	 * @param clazz the clazz to look in
	 * @param genericInterface the full method name to look for
	 * @param index the full method name to look for
	 * @return return
	 * @should find genericInterface on a superclass if clazz does not directly implement it
	 * @should ignore type variables on the declaring interface
	 * @should not inspect superclasses of the specified genericInterface
	 */
	@SuppressWarnings("rawtypes")
	public static Class getParameterizedTypeFromInterface(Class<?> clazz, Class<?> genericInterface, int index) {
		for (Type t : clazz.getGenericInterfaces()) {
			if (t instanceof ParameterizedType && ((Class) ((ParameterizedType) t).getRawType()).equals(genericInterface)) {
				//if we have reached the base interface that declares the type variable T, ignore it
				Type pType = ((ParameterizedType) t).getActualTypeArguments()[index];
				if (!(pType instanceof TypeVariable)) {
					return (Class) pType;
				}
			}
		}
		if (clazz.getSuperclass() != null && genericInterface.isAssignableFrom(clazz.getSuperclass())) {
			return getParameterizedTypeFromInterface(clazz.getSuperclass(), genericInterface, index);
		}
		return null;
	}
	
	/**
	 * @param <T> type
	 * @param handler handler
	 * @param propName propName
	 * @return return
	 */
	public static <T> Method findPropertyGetterMethod(DelegatingPropertyAccessor<? extends T> handler, String propName) {
		String key = handler.getClass().getName().concat(propName);
		Method result = getterMethodCache.get(key);
		if (result != null) {
			return result == nullMethod ? null : result;
		}
		
		Class<?> clazz = handler.getClass();
		while (clazz != Object.class && result == null) {
			for (Method method : clazz.getMethods()) {
				PropertyGetter ann = method.getAnnotation(PropertyGetter.class);
				if (ann != null && ann.value().equals(propName)) {
					result = method;
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		getterMethodCache.put(key, result == null ? nullMethod : result);
		
		return result;
	}
	
	/**
	 * @param <T> type
	 * @param handler handler
	 * @param propName propName
	 * @return return
	 */
	public static <T> Method findPropertySetterMethod(DelegatingPropertyAccessor<? extends T> handler, String propName) {
		String key = handler.getClass().getName().concat(propName);
		Method result = setterMethodCache.get(key);
		if (result != null) {
			return result == nullMethod ? null : result;
		}
		
		Class<?> clazz = handler.getClass();
		while (clazz != Object.class && result == null) {
			for (Method method : clazz.getMethods()) {
				PropertySetter ann = method.getAnnotation(PropertySetter.class);
				if (ann != null && ann.value().equals(propName)) {
					result = method;
					break;
				}
			}
			clazz = clazz.getSuperclass();
		}
		
		setterMethodCache.put(key, result == null ? nullMethod : result);
		
		return result;
	}
	
	/**
	 * @param name the full method name to look for
	 * @return the java Method object if found. (does not return null)
	 * @throws RuntimeException if not method found by the given name in the current class
	 * @param clazz
	 * @param propName
	 * @return
	 */
	public static Method findMethod(Class<?> clazz, String name) {
		Method ret = ReflectionUtils.findMethod(clazz, name, (Class<?>[]) null);
		if (ret == null)
			throw new RuntimeException("No suitable method \"" + name + "\" in " + clazz);
		return ret;
	}
	
}

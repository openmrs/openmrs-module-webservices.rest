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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HibernateLazyLoader {
	
	public <T> T load(T entity) {
		try {
			hibernateInit(entity);
			
			if (isHibernateProxy(entity)) {
				return (T) concreteClassOf(entity);
			}
		}
		catch (ClassNotFoundException e) {
			return entity;
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		return entity;
	}
	
	private <T> Object concreteClassOf(T entity) throws NoSuchMethodException, InvocationTargetException,
	        IllegalAccessException {
		return invoke("getImplementation", invoke("getHibernateLazyInitializer", entity));
	}
	
	private <T> void hibernateInit(T entity) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
	        InvocationTargetException {
		Class<?> hibernate = Class.forName("org.hibernate.Hibernate");
		Method initialize = hibernate.getMethod("initialize", Object.class);
		initialize.invoke(null, entity);
	}
	
	private <T> boolean isHibernateProxy(T entity) throws ClassNotFoundException {
		Class<?> proxyClass = Class.forName("org.hibernate.proxy.HibernateProxy");
		return proxyClass.isAssignableFrom(entity.getClass());
	}
	
	private Object invoke(String methodName, Object entity) throws NoSuchMethodException, InvocationTargetException,
	        IllegalAccessException {
		Method method = entity.getClass().getMethod(methodName);
		return method.invoke(entity);
	}
}

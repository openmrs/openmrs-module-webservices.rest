/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api.impl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link SchemaIntrospectionService}
 */
@Component
public class SchemaIntrospectionServiceImpl extends BaseOpenmrsService implements SchemaIntrospectionService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService#getDelegateType(Resource)
	 */
	@Override
	public Class<?> getDelegateType(Resource resource) {
		if (resource == null) {
			return null;
		}
		
		// Handle non-delegating resources
		if (!(resource instanceof DelegatingResourceHandler)) {
			log.warn("Resource " + resource.getClass().getName() + " is not a DelegatingResourceHandler");
			return null;
		}
		
		// Attempt to determine the delegate type from the generic parameter T in DelegatingCrudResource<T>
		// or DelegatingSubResource<T, P, PR>
		Class<?> resourceClass = resource.getClass();
		
		// Search through the class hierarchy to find the class that implements/extends with the generic type
		while (resourceClass != null) {
			Type[] genericInterfaces = resourceClass.getGenericInterfaces();
			for (Type genericInterface : genericInterfaces) {
				if (genericInterface instanceof ParameterizedType) {
					ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
					Type rawType = parameterizedType.getRawType();
					
					// Check if this interface is DelegatingResourceHandler or a subinterface of it
					if (rawType instanceof Class
					        && DelegatingResourceHandler.class.isAssignableFrom((Class<?>) rawType)) {
						// First type parameter should be the delegate type
						Type[] typeArgs = parameterizedType.getActualTypeArguments();
						if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
							return (Class<?>) typeArgs[0];
						}
					}
				}
			}
			
			// Check the superclass's generic type
			Type genericSuperclass = resourceClass.getGenericSuperclass();
			if (genericSuperclass instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
				Type[] typeArgs = parameterizedType.getActualTypeArguments();
				
				// First type argument should be the delegate type
				if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
					return (Class<?>) typeArgs[0];
				}
			}
			
			// Move up the hierarchy
			resourceClass = resourceClass.getSuperclass();
		}
		
		log.warn("Could not determine delegate type for " + resource.getClass().getName());
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService#discoverAvailableProperties(Class)
	 */
	@Override
	public Map<String, String> discoverAvailableProperties(Class<?> delegateType) {
		if (delegateType == null) {
			return new HashMap<String, String>();
		}
		
		Map<String, String> properties = new HashMap<String, String>();
		
		// Process fields up through the class hierarchy
		Class<?> currentClass = delegateType;
		while (currentClass != null && !currentClass.equals(Object.class)) {
			processFields(currentClass, properties);
			currentClass = currentClass.getSuperclass();
		}
		
		// Process getters - use Spring's BeanUtils to get all property descriptors
		PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(delegateType);
		for (PropertyDescriptor descriptor : propertyDescriptors) {
			// Skip "class" and properties without a readable method (getter)
			if ("class".equals(descriptor.getName()) || descriptor.getReadMethod() == null) {
				continue;
			}
			
			Method readMethod = descriptor.getReadMethod();
			
			// Only include public methods
			if (Modifier.isPublic(readMethod.getModifiers()) && !Modifier.isStatic(readMethod.getModifiers())) {
				// Get return type, including generic type if available
				String typeName = getTypeName(readMethod.getGenericReturnType());
				properties.put(descriptor.getName(), typeName);
			}
		}
		
		return properties;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.SchemaIntrospectionService#discoverResourceProperties(Resource)
	 */
	@Override
	public Map<String, String> discoverResourceProperties(Resource resource) {
		Class<?> delegateType = getDelegateType(resource);
		return discoverAvailableProperties(delegateType);
	}
	
	/**
	 * Helper method to process fields from a class and add them to the properties map
	 * 
	 * @param clazz The class to process fields from
	 * @param properties The map to add properties to
	 */
	private void processFields(Class<?> clazz, Map<String, String> properties) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			// Only include public instance fields
			if (Modifier.isPublic(field.getModifiers()) && !Modifier.isStatic(field.getModifiers())) {
				String typeName = getTypeName(field.getGenericType());
				properties.put(field.getName(), typeName);
			}
		}
	}
	
	/**
	 * Helper method to get a user-friendly type name from a Type object
	 * 
	 * @param type The type to get a name for
	 * @return A user-friendly type name string
	 */
	private String getTypeName(Type type) {
		if (type instanceof Class) {
			return ((Class<?>) type).getSimpleName();
		} else if (type instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) type;
			Type rawType = paramType.getRawType();
			Type[] typeArgs = paramType.getActualTypeArguments();
			
			StringBuilder sb = new StringBuilder();
			if (rawType instanceof Class) {
				sb.append(((Class<?>) rawType).getSimpleName());
			} else {
				sb.append(rawType.toString());
			}
			
			if (typeArgs.length > 0) {
				sb.append("<");
				for (int i = 0; i < typeArgs.length; i++) {
					if (i > 0) {
						sb.append(", ");
					}
					if (typeArgs[i] instanceof Class) {
						sb.append(((Class<?>) typeArgs[i]).getSimpleName());
					} else {
						sb.append(typeArgs[i].toString());
					}
				}
				sb.append(">");
			}
			
			return sb.toString();
		} else {
			return type.toString();
		}
	}
}
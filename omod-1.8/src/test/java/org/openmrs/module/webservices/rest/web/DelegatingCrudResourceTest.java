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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.User;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.UserResource1_8;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.util.Reflect;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ReflectionUtils;

/**
 * Contains tests for Representation Descriptions of all resources
 */
@Ignore
public class DelegatingCrudResourceTest extends BaseModuleWebContextSensitiveTest {

	/**
	 * This test looks at all subclasses of DelegatingCrudResource, and test all {@link RepHandler}
	 * methods to make sure they are all capable of running without exceptions. It also checks that
	 */
	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void testAllReprsentationDescriptions() throws Exception {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
		//only match subclasses of BaseDelegatingResource
		provider.addIncludeFilter(new AssignableTypeFilter(BaseDelegatingResource.class));
		
		// scan in org.openmrs.module.webservices.rest.web.resource package 
		Set<BeanDefinition> components = provider
		        .findCandidateComponents("org.openmrs.module.webservices.rest.web.resource");
		if (CollectionUtils.isEmpty(components))
			Assert.fail("Faile to load any resource classes");
		
		for (BeanDefinition component : components) {
			Class resourceClass = Class.forName(component.getBeanClassName());
			for (Method method : ReflectionUtils.getAllDeclaredMethods(resourceClass)) {
				ParameterizedType parameterizedType = (ParameterizedType) resourceClass.getGenericSuperclass();
				Class openmrsClass = (Class) parameterizedType.getActualTypeArguments()[0];
				//User Resource is special in that the Actual parameterized Type isn't a standard domain object, so we also
				//need to look up fields and methods from the org.openmrs.User class 
				boolean isUserResource = resourceClass.equals(UserResource1_8.class);
				List<Object> refDescriptions = new ArrayList<Object>();
				
				if (method.getName().equals("getRepresentationDescription")
				        && method.getDeclaringClass().equals(resourceClass)) {
					//get all the rep definitions for all representations
					refDescriptions.add(method.invoke(resourceClass.newInstance(), new Object[] { Representation.REF }));
					refDescriptions.add(method.invoke(resourceClass.newInstance(), new Object[] { Representation.DEFAULT }));
					refDescriptions.add(method.invoke(resourceClass.newInstance(), new Object[] { Representation.FULL }));
				}
				
				for (Object value : refDescriptions) {
					if (value != null) {
						DelegatingResourceDescription des = (DelegatingResourceDescription) value;
						for (String key : des.getProperties().keySet()) {
							if (!key.equals("uri") && !key.equals("display") && !key.equals("auditInfo")) {
								boolean hasFieldOrPropertySetter = (ReflectionUtils.findField(openmrsClass, key) != null);
								if (!hasFieldOrPropertySetter) {
									hasFieldOrPropertySetter = hasSetterMethod(key, resourceClass);
									if (!hasFieldOrPropertySetter && isUserResource)
										hasFieldOrPropertySetter = (ReflectionUtils.findField(User.class, key) != null);
								}
								if (!hasFieldOrPropertySetter)
									hasFieldOrPropertySetter = hasSetterMethod(key, resourceClass);
								
								//TODO replace this hacky way that we are using to check if there is a get method for a 
								//collection that has no actual getter e.g activeIdentifers and activeAttributes for Patient
								if (!hasFieldOrPropertySetter) {
									hasFieldOrPropertySetter = (ReflectionUtils.findMethod(openmrsClass, "get"
									        + StringUtils.capitalize(key)) != null);
									if (!hasFieldOrPropertySetter && isUserResource)
										hasFieldOrPropertySetter = (ReflectionUtils.findMethod(User.class, "get"
										        + StringUtils.capitalize(key)) != null);
								}
								
								if (!hasFieldOrPropertySetter)
									hasFieldOrPropertySetter = isallowedMissingProperty(resourceClass, key);
								
								Assert.assertTrue("No property found for '" + key + "' for " + openmrsClass
								        + " nor setter method on resource " + resourceClass, hasFieldOrPropertySetter);
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Convenience method that checks of the specified resource class has a method for setting the
	 * given property
	 * 
	 * @param propName
	 * @param resource
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static boolean hasSetterMethod(String propName, Class resourceClass) {
		for (Method candidate : resourceClass.getMethods()) {
			PropertySetter ann = candidate.getAnnotation(PropertySetter.class);
			if (ann != null && ann.value().equals(propName)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Convenience method that checks if the specified property is included among the allowed
	 * missing properties of the given resource class via reflection
	 * 
	 * @param clazz
	 * @param fieldName
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("rawtypes")
	private static boolean isallowedMissingProperty(Class resourceClass, String propName) throws IllegalArgumentException,
	        IllegalAccessException, InstantiationException {
		List<Field> fields = Reflect.getAllFields(resourceClass);
		if (CollectionUtils.isNotEmpty(fields)) {
			for (Field field : fields) {
				if (field.getName().equals("allowedMissingProperties"))
					return ((Set) field.get(resourceClass.newInstance())).contains(propName);
			}
		}
		return false;
	}
}

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

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ReflectionUtils;

/**
 * Contains tests for {@link DelegatingCrudResource}
 */
public class DelegatingCrudResourceTest extends BaseModuleContextSensitiveTest {
	
	/**
	 * This test looks at all subclasses of DelegatingCrudResource, and test all {@link RepHandler}
	 * methods to make sure they are all capable of running without exceptions.
	 */
	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void testAllMethodsWithRepHandlerAnnotation() throws Exception {
		ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(true);
		//only match DelegatingCrudResource and its subclasses
		provider.addIncludeFilter(new AssignableTypeFilter(DelegatingCrudResource.class));
		
		// scan in org.openmrs.module.webservices.rest.web.resource package 
		Set<BeanDefinition> components = provider
		        .findCandidateComponents("org.openmrs.module.webservices.rest.web.resource");
		for (BeanDefinition component : components) {
			Class clazz = Class.forName(component.getBeanClassName());
			//invoke all the methods with the RepHandler annotation to ensure they can run without exceptions
			for (Method method : ReflectionUtils.getAllDeclaredMethods(clazz)) {
				if (method.isAnnotationPresent(RepHandler.class)) {
					ParameterizedType parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
					method.invoke(clazz.newInstance(),
					    new Object[] { ((Class) parameterizedType.getActualTypeArguments()[0]).newInstance() });
				}
			}
		}
	}
}

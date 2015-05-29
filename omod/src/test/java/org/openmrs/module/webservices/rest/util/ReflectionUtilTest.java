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

import org.junit.Test;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_10.DrugOrderSubclassHandler1_10;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class ReflectionUtilTest {
	
	/**
	 * @verifies find genericInterface on a superclass if clazz does not directly implement it
	 * @see ReflectionUtil#getParameterizedTypeFromInterface(Class, Class, int)
	 */
	@Test
	public void getParameterizedTypeFromInterface_shouldFindGenericInterfaceOnASuperclassIfClazzDoesNotDirectlyImplementIt()
	        throws Exception {
		Class<?> expectedClass = ReflectionUtil.getParameterizedTypeFromInterface(DrugOrderSubclassHandler1_10.class,
		    DelegatingSubclassHandler.class, 0);
		assertEquals(Order.class, expectedClass);
		
		expectedClass = ReflectionUtil.getParameterizedTypeFromInterface(DrugOrderSubclassHandler1_10.class,
		    DelegatingSubclassHandler.class, 1);
		assertEquals(DrugOrder.class, expectedClass);
	}
	
	/**
	 * @verifies ignore type variables on the declaring interface
	 * @see ReflectionUtil#getParameterizedTypeFromInterface(Class, Class, int)
	 */
	@Test
	public void getParameterizedTypeFromInterface_shouldIgnoreTypeVariablesOnTheDeclaringInterface() throws Exception {
		//DelegatingResourceHandler<T> is one of the generic interfaces implemented by 
		//BaseDelegatingResource, once the logic reaches it, the type parameter T has to be ignored
		Class<?> clazz = ReflectionUtil.getParameterizedTypeFromInterface(BaseDelegatingResource.class,
		    DelegatingResourceHandler.class, 0);
		assertNull(clazz);
	}
	
	/**
	 * @verifies not inspect superclasses of the specified genericInterface
	 * @see ReflectionUtil#getParameterizedTypeFromInterface(Class, Class, int)
	 */
	@Test
	public void getParameterizedTypeFromInterface_shouldNotInspectSuperclassesOfTheSpecifiedGenericInterface()
	        throws Exception {
		Class<?> clazz = ReflectionUtil.getParameterizedTypeFromInterface(BaseDelegatingSubclassHandler.class,
		    DrugOrderSubclassHandler1_10.class, 1);
		assertNull(clazz);
	}
}

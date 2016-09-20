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
package org.openmrs.module.webservices.rest.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.openmrs.module.webservices.rest.web.resource.api.Resource;

/**
 * Indicates that the annotated class is a sub-resource of another Resource
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface SubResource {
	
	/**
	 * @return the resource class that this sub-resource is a child of
	 */
	Class<? extends Resource> parent();
	
	/**
	 * @return the relative URI this sub-resource lives at (will be appended to the URI of the
	 *         parent resource)
	 */
	String path();
	
	Class<?> supportedClass();
	
	String[] supportedOpenmrsVersions();
	
	int order() default Integer.MAX_VALUE;
	
}

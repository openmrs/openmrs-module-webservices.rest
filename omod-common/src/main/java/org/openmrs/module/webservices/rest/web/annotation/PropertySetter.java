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

import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;

/**
 * Use this annotation to mark a method in a {@link DelegatingCrudResource} implementation that
 * describes how to set a property on a delegate. (You would use this, for example, if you want to
 * expose a "preferredName" property in the resource, which doesn't have a direct setter on the
 * delegate.) The "setter" method should have the form "void setXyz(T delegate, Object value)" and
 * may be static.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PropertySetter {
	
	/**
	 * @return the name of the property the annotated method is a "setter" for.
	 */
	String value();
	
}

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
package org.openmrs.module.webservices.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be placed on *Resource class properties to help the
 * automatic translation change the output of the child property. E.g. on
 * EncounterResource, when the you ask for a "full" representation, the
 * "patient" property will only be displayed as "medium" because
 * EncounterResource.patient has this annotation:
 * <code>@WSCascade(cascadeFullAs="medium")</code> <br/>
 * If this annotation isn't present, it is assumed that the property should be
 * display as a Rep (or a list of Reps)
 * 
 */
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WSCascade {

	/**
	 * If the current resource has this annotation on a property, the property
	 * will be cascaded as the given representation value. Valid values are
	 * "full", "medium",or "default" <br/>
	 * <br/>
	 * If this is not filled in, the property is displayed as a "rep" with just
	 * uuid/uri/display
	 */
	public String cascadeFullAs() default "";

	/**
	 * If the current resource has this annotation on a property, the property
	 * will be cascaded as the given representation value. Valid values are
	 * "full", "medium",or "default" <br/>
	 * <br/>
	 * If this is not filled in, the property is displayed as a "rep" with just
	 * uuid/uri/display
	 */
	public String cascadeMediumAs() default "";

	/**
	 * If the current resource has this annotation on a property, the property
	 * will be cascaded as the given representation value. Valid values are
	 * "full", "medium",or "default"<br/>
	 * <br/>
	 * If this is not filled in, the property is displayed as a "rep" with just
	 * uuid/uri/display
	 */
	public String cascadeDefaultAs() default "";

}

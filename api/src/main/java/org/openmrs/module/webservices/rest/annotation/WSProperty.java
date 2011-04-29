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
 * This annotation should be placed on *Resource class properties when one of
 * the properties is going to be a default returned property or is one that is
 * required when creating. <br/>
 * E.g. on LocationResource name is default and would be annotated with
 * @WebServiceProperty(value={default, required})
 * 
 */
@Target( { ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WSProperty {

	/**
	 * This should be one of "default", "required" (for saving), or "minimal"<br/>
	 * <br/>
	 * (Minimal is still under debate)
	 */
	public String[] value() default {};

}

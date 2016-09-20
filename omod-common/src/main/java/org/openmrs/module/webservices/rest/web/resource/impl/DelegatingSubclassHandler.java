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
package org.openmrs.module.webservices.rest.web.resource.impl;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

/**
 * Implement and register one of these for each subclass handled by a class-hierarchy-supporting
 * resource
 */
public interface DelegatingSubclassHandler<Superclass, Subclass extends Superclass> extends DelegatingResourceHandler<Subclass> {
	
	/**
	 * @return the user-friendly name for the type this handles (e.g. "drugorder" for
	 *         org.openmrs.DrugOrder)
	 */
	String getTypeName();
	
	/**
	 * Convenience method that lets you retrieve the declared superclass at runtime without needing
	 * to use introspection yourself
	 * 
	 * @return
	 */
	Class<Superclass> getSuperclass();
	
	/**
	 * Convenience method that lets you retrieve the declared subclass at runtime without needing to
	 * use introspection yourself
	 * 
	 * @return
	 */
	Class<Subclass> getSubclassHandled();
	
	/**
	 * Gets all instances of this subclass of the resource
	 * 
	 * @param context
	 * @return
	 * @throws ResourceDoesNotSupportOperationException if this resource does not support the
	 *             operation
	 */
	PageableResult getAllByType(RequestContext context) throws ResourceDoesNotSupportOperationException;
	
}

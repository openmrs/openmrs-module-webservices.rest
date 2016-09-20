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

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Indicates that this resource or subclass can describe manage a delegate (save/purge) and describe
 * its representations via {@link DelegatingResourceDescription}
 * 
 * @param <T> the class of the delegate this resource handles
 */
public interface DelegatingResourceHandler<T> extends DelegatingPropertyAccessor<T> {
	
	/**
	 * Indicates a version of the supported resource.
	 * 
	 * @return the resource version
	 */
	String getResourceVersion();
	
	/**
	 * Instantiates a new instance of the handled delegate
	 * 
	 * @return
	 */
	T newDelegate();
	
	/**
	 * Instantiates a new instance of the handled delegate based on object's properties. It is used
	 * for example to create ConceptNumeric if datatype property is set to Numeric. It has a default
	 * implementation in {@link BaseDelegatingResource#newDelegate(SimpleObject)}, which delegates
	 * to {@link #newDelegate()}.
	 * 
	 * @param object
	 * @return
	 */
	T newDelegate(SimpleObject object);
	
	/**
	 * Writes the delegate to the database
	 * 
	 * @return the saved instance
	 */
	T save(T delegate);
	
	/**
	 * Purge delegate from persistent storage.
	 * 
	 * @param delegate
	 * @param context
	 * @throws ResponseException
	 */
	void purge(T delegate, RequestContext context) throws ResponseException;
	
	/**
	 * Gets the {@link DelegatingResourceDescription} for the given representation for this
	 * resource, if it exists
	 * 
	 * @param rep
	 * @return
	 */
	DelegatingResourceDescription getRepresentationDescription(Representation rep);
	
	/**
	 * Gets the {@link DelegatingResourceDescription} that describe how to create this resource
	 * 
	 * @return
	 * @throws ResponseException if this resource does not support the operation
	 */
	DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException;
	
	/**
	 * Gets the {@link DelegatingResourceDescription} that describes how to update this resource
	 * 
	 * @return
	 * @throws ResponseException if this resource does not support the operation
	 */
	DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException;
	
}

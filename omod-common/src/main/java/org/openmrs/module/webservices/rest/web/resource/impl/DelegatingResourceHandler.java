/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
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

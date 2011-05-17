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

import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;

/**
 * Holds information related to a REST web service request
 */
public class RequestContext {

	private Representation representation = new DefaultRepresentation();

	private Integer startIndex = 0;

	private Integer limit = RestUtil.getDefaultLimit();

	public RequestContext() {
	}

	/**
	 * @return the representation
	 */
	public Representation getRepresentation() {
		return representation;
	}

	/**
	 * @param representation
	 *            the representation to set
	 */
	public void setRepresentation(Representation representation) {
		this.representation = representation;
	}

	/**
	 * Should be used to limit the number of main results returned by search
	 * methods
	 * 
	 * @return the integer limit set in a request parameter
	 * @see RestUtil#getRequestContext(org.springframework.web.context.request.WebRequest)
	 * @see RestConstants#REQUEST_PROPERTY_FOR_LIMIT
	 */
	public Integer getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            the limit to set
	 */
	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	/**
	 * Should be used by search methods to jump results to start with this
	 * number in the list. Set by users in a request parameter
	 * 
	 * @return the integer startIndex
	 * @see RestUtil#getRequestContext(org.springframework.web.context.request.WebRequest)
	 * @see RestConstants#REQUEST_PROPERTY_FOR_START_INDEX
	 */
	public Integer getStartIndex() {
		return startIndex;
	}

	/**
	 * @param startIndex
	 *            the startIndex to set
	 */
	public void setStartIndex(Integer startIndex) {
		this.startIndex = startIndex;
	}

}

/**
 * The contents of this file are subject 
 * to the OpenMRS Public License
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
package org.openmrs.module.webservices.rest.web.representation;

import org.openmrs.module.webservices.rest.web.annotation.RepHandler;

/**
 * A named representation, like "minimal"
 */
public class NamedRepresentation implements Representation {
	
	private String representation;
	
	public NamedRepresentation(String representation) {
		if (representation == null)
			throw new IllegalArgumentException("representation is required");
		this.representation = representation;
	}
	
	/**
	 * @return the representation
	 * @see Representation#getRepresentation()
	 */
	@Override
	public String getRepresentation() {
		return representation;
	}
	
	/**
	 * @param representation the representation to set
	 */
	public void setRepresentation(String representation) {
		this.representation = representation;
	}
	
	/**
	 * @param ann
	 * @return true if ann is for {@link NamedRepresentation} with the correct name specified
	 */
	public boolean matchesAnnotation(RepHandler ann) {
		return NamedRepresentation.class.isAssignableFrom(ann.value()) && ann.name().equals(representation);
	}
	
}

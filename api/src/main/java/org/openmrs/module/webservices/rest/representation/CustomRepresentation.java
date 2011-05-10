package org.openmrs.module.webservices.rest.representation;

/**
 * Implementation of {@link Representation} where the user specifies which properties they want
 * included
 */
public class CustomRepresentation implements Representation {
	
	String specification;
	
	public CustomRepresentation(String specification) {
		this.specification = specification;
	}
	
}

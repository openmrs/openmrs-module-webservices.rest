package org.openmrs.module.webservices.rest.web.representation;

import org.openmrs.module.webservices.rest.web.annotation.RepHandler;

/**
 * A named representation, like "minimal" or "full"
 */
public class NamedRepresentation implements Representation {
	
	private String representation;
	
	public NamedRepresentation(String representation) {
		if (representation == null)
			representation = "default";
		this.representation = representation;
	}
	
	/**
	 * @return the representation
	 */
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
		return ann.name().equals(representation);
	}
	
}

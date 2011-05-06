package org.openmrs.module.webservices.rest;

import org.openmrs.module.webservices.rest.annotation.IncludeProperties;

/**
 * A named representation, like "default", "minimal", or "full"
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
     * @see org.openmrs.module.webservices.rest.Representation#matchesAnnotation(org.openmrs.module.webservices.rest.annotation.IncludeProperties)
     */
    public boolean matchesAnnotation(IncludeProperties incProp) {
	    return incProp.rep().equals(representation);
    }

}

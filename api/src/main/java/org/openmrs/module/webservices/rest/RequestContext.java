package org.openmrs.module.webservices.rest;

/**
 * Holds information related to a REST web service request
 */
public class RequestContext {
	
	private Representation representation = new NamedRepresentation("default");
	
	private Integer limit;

	public RequestContext() { }
	
    /**
     * @return the representation
     */
    public Representation getRepresentation() {
    	return representation;
    }

	
    /**
     * @param representation the representation to set
     */
    public void setRepresentation(Representation representation) {
    	this.representation = representation;
    }

	
    /**
     * @return the limit
     */
    public Integer getLimit() {
    	return limit;
    }

	
    /**
     * @param limit the limit to set
     */
    public void setLimit(Integer limit) {
    	this.limit = limit;
    }

    
}

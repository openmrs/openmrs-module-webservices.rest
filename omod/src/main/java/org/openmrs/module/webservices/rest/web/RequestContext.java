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

	
    /**
     * @return the startIndex
     */
    public Integer getStartIndex() {
    	return startIndex;
    }

	
    /**
     * @param startIndex the startIndex to set
     */
    public void setStartIndex(Integer startIndex) {
    	this.startIndex = startIndex;
    }
    
}

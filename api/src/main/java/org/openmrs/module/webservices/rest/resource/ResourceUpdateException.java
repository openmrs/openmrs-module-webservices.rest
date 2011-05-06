package org.openmrs.module.webservices.rest.resource;


public class ResourceUpdateException extends Exception {

    private static final long serialVersionUID = 1L;

	public ResourceUpdateException(Exception ex) {
	    super(ex);
    }

}

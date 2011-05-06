package org.openmrs.module.webservices.rest.resource;


public class ResourceDeletionException extends Exception {

    private static final long serialVersionUID = 1L;
	
    public ResourceDeletionException(Exception ex) {
	    super(ex);
    }

	public ResourceDeletionException(String message) {
	    super(message);
    }

}

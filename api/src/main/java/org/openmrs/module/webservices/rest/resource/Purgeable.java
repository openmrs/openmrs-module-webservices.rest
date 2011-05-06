package org.openmrs.module.webservices.rest.resource;


/**
 * Interface implemented by resources that support the standard-pattern Purge operation
 * (i.e. a DELETE on the resource, with a purge=true parameter
 */
public interface Purgeable extends Resource {
	
	void purge(String uuid) throws ResourceDeletionException;

}

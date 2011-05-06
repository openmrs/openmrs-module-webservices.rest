package org.openmrs.module.webservices.rest.resource;


/**
 * Interface implemented by resources that support the standard-pattern Delete operation
 */
public interface Deletable extends Resource {
	
	/**
	 * Deletes the specified resource, which in the OpenMRS context means either voiding or retiring it
	 * @param uuid
	 * @param reason
	 */
	void delete(String uuid, String reason) throws ResourceDeletionException;

}

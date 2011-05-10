package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.webservices.rest.RequestContext;


/**
 * Interface implemented by resources that support the standard-pattern Delete operation
 */
public interface Deletable extends Resource {
	
	/**
	 * Deletes the specified resource, which in the OpenMRS context means either voiding or retiring it
	 * @param uuid
	 * @param reason
	 * @param context
	 */
	void delete(String uuid, String reason, RequestContext context) throws ResourceDeletionException;

}

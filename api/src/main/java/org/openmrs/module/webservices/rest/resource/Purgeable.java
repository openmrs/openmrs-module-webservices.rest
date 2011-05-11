package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Interface implemented by resources that support the standard-pattern Purge operation (i.e. a
 * DELETE on the resource, with a purge=true parameter)
 */
public interface Purgeable extends Resource {
	
	/**
	 * Deletes a resources from persistent storage, so that it no longer exists, not even in a
	 * voided or retired state.
	 * 
	 * @param uuid
	 * @param context
	 * @throws ObjectNotFoundException
	 * @throws ResponseException
	 */
	void purge(String uuid, RequestContext context) throws ResponseException;
	
}

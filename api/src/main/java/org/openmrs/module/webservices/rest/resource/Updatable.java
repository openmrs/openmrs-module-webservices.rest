package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;

/**
 * Interface implemented by resources that have the standard-pattern Update operation, which takes a map
 * of properties to update on the resource
 */
public interface Updatable extends Resource {
	
	/**
	 * Sets the given properties on the resource identified by the given uuid
	 * @param uuid 
	 * @param propertiesToUpdate
	 * @param context
	 * @return
	 * @throws ResourceUpdateException 
	 */
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResourceUpdateException;

}

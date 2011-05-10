package org.openmrs.module.webservices.rest.resource;

import org.openmrs.module.webservices.rest.RequestContext;

/**
 * Interface implemented by resources that have the standard-pattern Retrieve operation by UUID
 */
public interface Retrievable extends Resource {
	
	/**
	 * Gets the object with the given uuid, in the given representation
	 * @param uuid
	 * @param context
	 * @return
	 */
	public Object retrieve(String uuid, RequestContext context);

}

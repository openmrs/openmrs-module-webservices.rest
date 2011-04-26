/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.resource;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSUtil;

/**
 * Marker interface for all OpenmrsResources
 */
public interface OpenmrsResource<T extends OpenmrsObject> {

	/**
	 * The string to put into the "display" property when <code>T</code> is
	 * turned into a "Ref" object. <br/>
	 * e.g. for a Patient it might be "NAME (IDENTIFIER)" like
	 * "John A Doe (123MT-2)"
	 * 
	 * @param openmrsObject
	 *            the object being converted into a {@link SimpleObject}
	 * @return a string that is helpful for the user.
	 */
	String getDisplay(T openmrsObject);

	/**
	 * The string to put into the "uri" property when <code>T</code> is turned
	 * into a "Ref" object <br/>
	 * e.g. for a Patient it would be:<br/>
	 * <code>"patient/" + oo.getUuid()</code> <br/>
	 * but for a PersonName (a subresource) it would be:<br/>
	 * <code> "person/" + oo.getPerson().getUuid() + "/name/" + oo.getUuid()</code>
	 * 
	 * @param openmrsObject
	 *            the object being converted into a {@link SimpleObject}
	 * @return the link that will fetch the full object representation
	 * 
	 * @see WSUtil#getURI(OpenmrsResource, OpenmrsObject)
	 */
	String getURISuffix(T openmrsObject);

	// temporary, will be replace with annotations
	String[] getDefaultRepresentation();

}

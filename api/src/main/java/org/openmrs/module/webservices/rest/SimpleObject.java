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
package org.openmrs.module.webservices.rest;

import java.util.LinkedHashMap;

import org.openmrs.OpenmrsObject;
import org.openmrs.module.webservices.rest.resource.OpenmrsResource;
import org.openmrs.util.HandlerUtil;

/**
 * This is the Map returned for all objects. The properties are just key/value
 * pairs. If an object has subobjects those are just lists of SimpleObjects
 */
public class SimpleObject extends LinkedHashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public SimpleObject() {
		super();
	}

	/**
	 * Adds the link/uuid/display properties that every object should have.
	 * 
	 * @param resource
	 *            the OpenmrsResource to use to get the "link" and "display"
	 * @param oo
	 *            the OpenmrsObject being converted (has the uuids and values in
	 *            it)
	 */
	public SimpleObject(OpenmrsResource<OpenmrsObject> resource,
			OpenmrsObject oo) {
		this();
		put("uuid", oo.getUuid());

		if (resource == null) {
			// this could fail for multiple reasons, use
			// #SimpleObject(OpenmrsObject) instead
			String type = oo.getClass().getSimpleName().toLowerCase();
			put("uri", WSConstants.URL_PREFIX + type + "/" + oo.getUuid());
			put("display", oo.toString());
		} else {
			put("uri", resource.getURISuffix(oo));
			put("display", resource.getDisplay(oo));
		}

	}

	/**
	 * Convenience constructor that calls
	 * {@link #SimpleObject(OpenmrsResource, OpenmrsObject)} with a null
	 * resource object
	 * 
	 * @param oo
	 */
	@SuppressWarnings("unchecked")
	public SimpleObject(OpenmrsObject oo) {
		this(HandlerUtil.getPreferredHandler(OpenmrsResource.class, oo
				.getClass()), oo);
	}

}

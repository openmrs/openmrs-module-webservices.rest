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
package org.openmrs.module.webservices.rest.api.impl;

import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsObject;
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.CustomRepresentation;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.RefRepresentation;
import org.openmrs.module.webservices.rest.Representation;
import org.openmrs.module.webservices.rest.WSConstants;
import org.openmrs.module.webservices.rest.api.RestService;
import org.openmrs.module.webservices.rest.api.db.RestDAO;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	private RestDAO dao;
	
	/**
	 * @see org.openmrs.module.webservices.rest.api.RestService#getOpenmrsObjectByUuid(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T extends OpenmrsObject> T getOpenmrsObjectByUuid(Class<T> clazz, String uuid) {
		return dao.getOpenmrsObjectByUuid(clazz, uuid);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.api.RestService#getRepresentation(java.lang.String)
	 */
	@Override
	public Representation getRepresentation(String requested) {
	    if (StringUtils.isEmpty(requested)) {
	    	return new NamedRepresentation(WSConstants.REPRESENTATION_DEFAULT);
	    } else {
	    	if (WSConstants.REPRESENTATION_REF.equalsIgnoreCase(requested)) {
				return new RefRepresentation();
	    	} else if (WSConstants.REPRESENTATION_DEFAULT.equalsIgnoreCase(requested)) {
				return new NamedRepresentation(WSConstants.REPRESENTATION_DEFAULT);
			} else if (WSConstants.REPRESENTATION_MEDIUM.equalsIgnoreCase(requested)) {
				return new NamedRepresentation(WSConstants.REPRESENTATION_MEDIUM);
			} else if (WSConstants.REPRESENTATION_FULL.equalsIgnoreCase(requested)) {
				return new NamedRepresentation(WSConstants.REPRESENTATION_FULL);
			} else if (requested.startsWith(WSConstants.REPRESENTATION_CUSTOM_PREFIX)) {
				return new CustomRepresentation(requested.replace(WSConstants.REPRESENTATION_CUSTOM_PREFIX, ""));
			}
	    }
	    throw new APIException("Unknown representation: " + requested);
	}
}

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
import org.openmrs.api.APIException;
import org.openmrs.module.webservices.rest.RestConstants;
import org.openmrs.module.webservices.rest.api.RestService;
import org.openmrs.module.webservices.rest.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.representation.Representation;

/**
 * Default implementation of the {@link RestService}
 */
public class RestServiceImpl implements RestService {
	
	/**
	 * @see org.openmrs.module.webservices.rest.api.RestService#getRepresentation(java.lang.String)
	 */
	@Override
	public Representation getRepresentation(String requested) {
	    if (StringUtils.isEmpty(requested)) {
	    	return Representation.DEFAULT;
	    } else {
	    	if (RestConstants.REPRESENTATION_REF.equalsIgnoreCase(requested)) {
				return Representation.REF;
	    	} else if (RestConstants.REPRESENTATION_DEFAULT.equalsIgnoreCase(requested)) {
				return Representation.DEFAULT;
			} else if (RestConstants.REPRESENTATION_FULL.equalsIgnoreCase(requested)) {
				return Representation.FULL;
			} else if (RestConstants.REPRESENTATION_MEDIUM.equalsIgnoreCase(requested)) {
				return new NamedRepresentation(RestConstants.REPRESENTATION_MEDIUM);
			} else if (requested.startsWith(RestConstants.REPRESENTATION_CUSTOM_PREFIX)) {
				return new CustomRepresentation(requested.replace(RestConstants.REPRESENTATION_CUSTOM_PREFIX, ""));
			}
	    }
	    throw new APIException("Unknown representation: " + requested);
	}
}

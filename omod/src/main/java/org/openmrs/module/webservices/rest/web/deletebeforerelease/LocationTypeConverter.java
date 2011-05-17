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
package org.openmrs.module.webservices.rest.web.deletebeforerelease;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Temporary, for testing creating a patient. Delete this when we implement the LocationResource
 */
@Handler(supports = Location.class, order = 0)
public class LocationTypeConverter implements Converter<Location> {
	
	@Override
	public Location getByUniqueId(String uuidOrName) {
		Location ret = Context.getLocationService().getLocationByUuid(uuidOrName);
		/* This method doesn't exist
		if (ret == null)
			ret = Context.getLocationService().getLocationByName(uuidOrName);
		*/
		return ret;
	}
	
	@Override
	public Object asRepresentation(Location instance, Representation rep) throws ConversionException {
		return "Temp hack some location";
		//throw new RuntimeException("Not implemented");
	}
	
	@Override
	public Object getProperty(Location instance, String propertyName) throws ConversionException {
		try {
			return PropertyUtils.getProperty(instance, propertyName);
		}
		catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
	}
	
	@Override
	public void setProperty(Location instance, String propertyName, Object value) throws ConversionException {
		try {
			PropertyUtils.setProperty(instance, propertyName, value);
		}
		catch (Exception ex) {
			throw new ConversionException(null, ex);
		}
	}
	
}

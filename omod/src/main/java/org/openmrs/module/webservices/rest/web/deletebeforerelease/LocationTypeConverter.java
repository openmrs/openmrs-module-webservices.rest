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

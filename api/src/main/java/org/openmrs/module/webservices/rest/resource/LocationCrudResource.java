package org.openmrs.module.webservices.rest.resource;

import org.openmrs.Location;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.annotation.Resource;

@Resource("location")
@Handler(supports=Location.class)
public class LocationCrudResource extends MetadataDelegatingCrudResource<Location> {
	
    public LocationCrudResource() {
    	super(null);
    }
	
	public LocationCrudResource(Location location) {
		super(location);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Location newDelegate() {
	    return new Location();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#saveDelegate()
	 */
	@Override
	public Location saveDelegate() {
	    return Context.getLocationService().saveLocation(delegate);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegateConverter#fromString(java.lang.String)
	 */
	@Override
    public Location fromString(String uuidOrName) {
		Location ret = Context.getLocationService().getLocationByUuid(uuidOrName);
		if (ret == null)
			ret = Context.getLocationService().getLocation(uuidOrName);
		return ret;
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#purge()
	 */
	@Override
	public void purge() throws ResourceDeletionException {
	    try {
	    	Context.getLocationService().purgeLocation(delegate);
	    } catch (Exception ex) {
	    	throw new ResourceDeletionException(ex);
	    }
	}

}

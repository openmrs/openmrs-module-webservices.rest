package org.openmrs.module.webservices.rest.resource;

import org.openmrs.OpenmrsData;
import org.openmrs.module.webservices.rest.RefRepresentation;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.IncludeProperties;
import org.openmrs.module.webservices.rest.annotation.RepClassHandler;

@IncludeProperties(rep="default", properties={ "uuid" })
public abstract class DataDelegatingCrudResource<T extends OpenmrsData> extends DelegatingCrudResource<T> {
	
	protected DataDelegatingCrudResource(T delegate) {
		super(delegate);
	}
	
    @RepClassHandler(RefRepresentation.class)
    public SimpleObject convertToRef(RefRepresentation rep) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.toString());
    	return ret;
    }
    
    /* This doesn't work because it doesn't cascade to children.
    @Override
    public void delete(String reason) throws ResourceDeletionException {
        if (delegate.isVoided())
        	throw new ResourceDeletionException("Already voided");
        delegate.setVoided(true);
        delegate.setVoidedBy(Context.getAuthenticatedUser());
        delegate.setDateVoided(new Date());
        delegate.setVoidReason(reason);
    }
    */

}

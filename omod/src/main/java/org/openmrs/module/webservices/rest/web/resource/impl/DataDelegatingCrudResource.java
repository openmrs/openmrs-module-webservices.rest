package org.openmrs.module.webservices.rest.web.resource.impl;

import org.openmrs.OpenmrsData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;

/**
 * Subclass of {@link DelegatingCrudResource} with helper methods specific to {@link OpenmrsData}
 * @param <T>
 */
public abstract class DataDelegatingCrudResource<T extends OpenmrsData> extends DelegatingCrudResource<T> {
	
	protected DataDelegatingCrudResource(T delegate) {
		super(delegate);
	}
	
    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(RefRepresentation rep) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.toString());
    	ret.put("uri", "something/" + getUriFragment() + "/" + delegate.getUuid());
    	return ret;
    }
    
    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep() throws Exception {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.toString());
    	ret.put("temp-hack", "this method should be overridden");
    	return ret;
    }
    
    public SimpleObject getAuditInfo() throws Exception {
    	SimpleObject ret = new SimpleObject();
    	ret.put("creator", getPropertyWithRepresentation("creator", new RefRepresentation()));
    	ret.put("dateCreated", delegate.getDateCreated());
    	return ret;
    }
    
}

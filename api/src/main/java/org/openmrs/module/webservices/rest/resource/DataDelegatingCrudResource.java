package org.openmrs.module.webservices.rest.resource;

import org.openmrs.OpenmrsData;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.RefRepresentation;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;

public abstract class DataDelegatingCrudResource<T extends OpenmrsData> extends DelegatingCrudResource<T> {
	
	protected DataDelegatingCrudResource(T delegate) {
		super(delegate);
	}
	
    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(RefRepresentation rep) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.toString());
    	return ret;
    }
    
    @RepHandler(value=NamedRepresentation.class, name="default")
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

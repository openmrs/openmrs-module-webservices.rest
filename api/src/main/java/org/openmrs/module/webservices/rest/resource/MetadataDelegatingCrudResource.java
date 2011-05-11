package org.openmrs.module.webservices.rest.resource;

import java.util.Date;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;
import org.openmrs.module.webservices.rest.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Subclass of {@link DelegatingCrudResource} with helper methods specific to {@link OpenmrsMetadata}
 * @param <T>
 */
public abstract class MetadataDelegatingCrudResource<T extends OpenmrsMetadata> extends DelegatingCrudResource<T> {
	
    protected MetadataDelegatingCrudResource(T metadata) {
	    super(metadata);
    }

    @RepHandler(RefRepresentation.class)
    public SimpleObject convertToRef(RefRepresentation rep) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.getName());
    	ret.put("uri", "something/" + getUriFragment() + "/" + delegate.getUuid());
    	return ret;
    }
    
    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep() throws Exception {
    	DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("description");
		return convertDelegateToRepresentation(rep);
    }

	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#delete(java.lang.String, org.openmrs.module.webservices.rest.RequestContext)
	 */
	@Override
    public void delete(String reason, RequestContext context) throws ResponseException {
        if (delegate.isRetired()) {
        	// since DELETE should be idempotent, we return success here
        	return;
        }
        delegate.setRetired(true);
        delegate.setRetiredBy(Context.getAuthenticatedUser());
        delegate.setDateRetired(new Date());
        delegate.setRetireReason(reason);
        saveDelegate();
    }
}

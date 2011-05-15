package org.openmrs.module.webservices.rest.web.resource.impl;

import java.util.Date;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Subclass of {@link DelegatingCrudResource} with helper methods specific to {@link OpenmrsMetadata}
 * @param <T>
 */
public abstract class MetadataDelegatingCrudResource<T extends OpenmrsMetadata> extends DelegatingCrudResource<T> {

    @RepHandler(RefRepresentation.class)
    public SimpleObject convertToRef(T delegate) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.getName());
    	ret.put("uri", getUri(delegate));
    	return ret;
    }
    
    @RepHandler(DefaultRepresentation.class)
    public SimpleObject asDefaultRep(T delegate) throws Exception {
    	DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("description");
		rep.addProperty("uri", getClass().getMethod("getUri"));
		return convertDelegateToRepresentation(delegate, rep);
    }

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
    public void delete(T delegate, String reason, RequestContext context) throws ResponseException {
        if (delegate.isRetired()) {
        	// since DELETE should be idempotent, we return success here
        	return;
        }
        delegate.setRetired(true);
        delegate.setRetiredBy(Context.getAuthenticatedUser());
        delegate.setDateRetired(new Date());
        delegate.setRetireReason(reason);
        save(delegate);
    }
}

package org.openmrs.module.webservices.rest.resource;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.openmrs.OpenmrsMetadata;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RefRepresentation;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.IncludeProperties;
import org.openmrs.module.webservices.rest.annotation.RepClassHandler;
import org.openmrs.module.webservices.rest.annotation.Resource;

@IncludeProperties(rep="default", properties={ "uuid", "name", "description" })
public abstract class MetadataDelegatingCrudResource<T extends OpenmrsMetadata> extends DelegatingCrudResource<T> {
	
    protected MetadataDelegatingCrudResource(T metadata) {
	    super(metadata);
    }

    @RepClassHandler(RefRepresentation.class)
    public SimpleObject convertToRef(RefRepresentation rep) {
    	SimpleObject ret = new SimpleObject();
    	ret.put("uuid", delegate.getUuid());
    	ret.put("display", delegate.getName());
    	ret.put("uri", "something/" + getUriFragment() + "/" + delegate.getUuid());
    	return ret;
    }

    /**
     * Gets the URI fragment from the @RestResource annotation on the concrete subclass
     * @return
     */
    private String getUriFragment() {
    	Resource ann = getClass().getAnnotation(Resource.class);
    	if (ann == null)
    		throw new RuntimeException("There is no " + Resource.class + " annotation on " + getClass());
    	if (StringUtils.isEmpty(ann.value()))
    		throw new RuntimeException(Resource.class.getSimpleName() + " annotation on " + getClass() + " must specify a value");
    	return ann.value();
    }

	@Override
    public void delete(String reason, RequestContext context) throws ResourceDeletionException {
        if (delegate.isRetired())
        	throw new ResourceDeletionException("Already retired");
        delegate.setRetired(true);
        delegate.setRetiredBy(Context.getAuthenticatedUser());
        delegate.setDateRetired(new Date());
        delegate.setRetireReason(reason);
        saveDelegate();
    }
}

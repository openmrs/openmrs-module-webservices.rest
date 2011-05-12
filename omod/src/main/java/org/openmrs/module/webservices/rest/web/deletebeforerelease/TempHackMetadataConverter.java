package org.openmrs.module.webservices.rest.web.deletebeforerelease;

import org.openmrs.OpenmrsMetadata;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.api.DelegateConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * This class is a temporary hack, to be removed before the first release of the module. It allows
 * the framework to generate resource representations that refer to metadata properties that don't
 * have their own {@link DelegateConverter} defined yet without getting the 'Conflicting getter
 * definitions for property "retired"' you would otherwise get from Jackson JSON.
 */
@Resource("temp hack")
@Handler(supports=OpenmrsMetadata.class)
public class TempHackMetadataConverter extends MetadataDelegatingCrudResource<OpenmrsMetadata> {
	
	@Override
	public void purge(OpenmrsMetadata md, RequestContext context) throws ResponseException {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsMetadata save(OpenmrsMetadata md) {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsMetadata newDelegate() {
		throw new RuntimeException("Not Implemented");
	}

	@Override
    public OpenmrsMetadata getByUniqueId(String uniqueId) {
		throw new RuntimeException("Not Implemented");
    }
	
}

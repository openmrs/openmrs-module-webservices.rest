package org.openmrs.module.webservices.rest.web.deletebeforerelease;

import org.openmrs.OpenmrsData;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.DelegateConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * This class is a temporary hack, to be removed before the first release of the module. It allows
 * the framework to generate resource representations that refer to metadata properties that don't
 * have their own {@link DelegateConverter} defined yet without getting the 'Conflicting getter
 * definitions for property "voided"' you would otherwise get from Jackson JSON.
 */
@Resource("temp hack")
@Handler(supports = OpenmrsData.class)
public class TempHackDataConverter extends DataDelegatingCrudResource<OpenmrsData> {

	@Override
	public void purge(OpenmrsData data, RequestContext context) throws ResponseException {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsData save(OpenmrsData data) {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsData newDelegate() {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public void delete(OpenmrsData data, String reason, RequestContext context) throws ResponseException {
		throw new RuntimeException("Not Implemented");
	}

	@Override
    public OpenmrsData getByUniqueId(String uniqueId) {
		throw new RuntimeException("Not Implemented");
    }

	@Override
    public DelegatingResourceRepresentation getRepresentationDescription(Representation rep) {
		throw new RuntimeException("Not Implemented");
    }
	
}

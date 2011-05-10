package org.openmrs.module.webservices.rest.deletebeforerelease;

import org.openmrs.OpenmrsData;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.annotation.Resource;
import org.openmrs.module.webservices.rest.resource.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.resource.DelegateConverter;
import org.openmrs.module.webservices.rest.resource.ResourceDeletionException;

/**
 * This class is a temporary hack, to be removed before the first release of the module. It allows
 * the framework to generate resource representations that refer to metadata properties that don't
 * have their own {@link DelegateConverter} defined yet without getting the 'Conflicting getter
 * definitions for property "voided"' you would otherwise get from Jackson JSON.
 */
@Resource("temp hack")
@Handler(supports = OpenmrsData.class)
public class TempHackDataConverter extends DataDelegatingCrudResource<OpenmrsData> {
	
	public TempHackDataConverter() {
		super(null);
	}
	
	@Override
	public OpenmrsData fromString(String string) {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public void purge(RequestContext context) throws ResourceDeletionException {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsData saveDelegate() {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	protected OpenmrsData newDelegate() {
		throw new RuntimeException("Not Implemented");
	}
	
	@Override
	public void delete(String reason, RequestContext context) throws ResourceDeletionException {
		throw new RuntimeException("Not Implemented");
	}
	
}

package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.ConceptName;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptNameResourceTest extends BaseDelegatingResourceTest<ConceptName> {
	
	@Override
	public BaseDelegatingResource<ConceptName> getResource() {
		return Context.getService(RestService.class).getResource(ConceptNameResource.class);
	}
	
	@Override
	public ConceptName getObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getName();
	}
	
}

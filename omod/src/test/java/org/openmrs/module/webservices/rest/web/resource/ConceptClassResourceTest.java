package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.ConceptClass;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptClassResourceTest extends BaseDelegatingResourceTest<ConceptClass> {
	
	@Override
	public BaseDelegatingResource<ConceptClass> getResource() {
		return Context.getService(RestService.class).getResource(ConceptClassResource.class);
	}
	
	@Override
	public ConceptClass getObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getConceptClass();
	}
	
}

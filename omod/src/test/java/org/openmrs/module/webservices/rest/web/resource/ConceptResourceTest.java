package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptResourceTest extends BaseDelegatingResourceTest<Concept> {
	
	@Override
	public BaseDelegatingResource<Concept> getResource() {
		return Context.getService(RestService.class).getResource(ConceptResource.class);
	}
	
	@Override
	public Concept getObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID);
	}
	
}

package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.ConceptDescription;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptDescriptionResourceTest extends BaseDelegatingResourceTest<ConceptDescription> {
	
	@Override
	public BaseDelegatingResource<ConceptDescription> getResource() {
		return Context.getService(RestService.class).getResource(ConceptDescriptionResource.class);
	}
	
	@Override
	public ConceptDescription getObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getDescription();
	}
	
}

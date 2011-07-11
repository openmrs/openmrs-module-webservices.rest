package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.ConceptDatatype;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResourceTest;

public class ConceptDatatypeResourceTest extends BaseDelegatingResourceTest<ConceptDatatype> {
	
	@Override
	public BaseDelegatingResource<ConceptDatatype> getResource() {
		return Context.getService(RestService.class).getResource(ConceptDatatypeResource.class);
	}
	
	@Override
	public ConceptDatatype getObject() {
		return Context.getConceptService().getConceptByUuid(ResourceTestConstants.CONCEPT_UUID).getDatatype();
	}
	
}

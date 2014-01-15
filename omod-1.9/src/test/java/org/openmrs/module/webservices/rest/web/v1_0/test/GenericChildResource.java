package org.openmrs.module.webservices.rest.web.v1_0.test;

import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/genericChild", supportedClass = GenericChild.class, supportedOpenmrsVersions = "1.9.*")
public class GenericChildResource extends DelegatingCrudResource<GenericChild> {
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("value");
		description.addSelfLink();
		description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);

		return description;
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("value");

		return description;
	}

	@Override
	public GenericChild newDelegate() {
		return new GenericChild();
	}

	@Override
	public GenericChild save(GenericChild child) {
		return child;
	}

	@Override
	public GenericChild getByUniqueId(String uuid) {
		return null;
	}

	@Override
	public void delete(GenericChild visit, String reason, RequestContext context) throws ResponseException {
	}

	@Override
	public void purge(GenericChild visit, RequestContext context) throws ResponseException {
	}

	@Override
	public String getResourceVersion() {
		return "1.9";
	}
}

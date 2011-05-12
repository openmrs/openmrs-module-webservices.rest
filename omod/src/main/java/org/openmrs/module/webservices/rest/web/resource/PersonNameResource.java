package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for PersonNames, supporting standard CRUD operations
 */
@Resource("personName")
@Handler(supports=PersonName.class, order=0)
public class PersonNameResource extends DataDelegatingCrudResource<PersonName> {

	@Override
	public DelegatingResourceRepresentation getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceRepresentation description = new DelegatingResourceRepresentation();
			description.addProperty("givenName");
			description.addProperty("middleName");
			description.addProperty("familyName");
			description.addProperty("familyName2");
			description.addMethodProperty("uri", findMethod("getUri"));
			return description;
		}
	    return null;
	}

	@Override
	public PersonName getByUniqueId(String uuid) {
		return Context.getPersonService().getPersonNameByUuid(uuid);
	}
	
	@Override
	public void delete(PersonName pn, String reason, RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub
	}
	
	@Override
	public void purge(PersonName pn, RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub	
	}
	
	@Override
	protected PersonName save(PersonName newName) {
		// make sure that the name has actually been added to the person
		boolean needToAdd = true;
		for (PersonName pn : newName.getPerson().getNames()) {
			if (pn.equals(newName)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			newName.getPerson().addName(newName);
		Context.getPersonService().savePerson(newName.getPerson());
		return newName;
	}
	
	@Override
	protected PersonName newDelegate() {
		return new PersonName();
	}
	
}

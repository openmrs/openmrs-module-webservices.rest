package org.openmrs.module.webservices.rest.resource;

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;
import org.openmrs.module.webservices.rest.annotation.Resource;
import org.openmrs.module.webservices.rest.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for PersonNames, supporting standard CRUD operations
 */
@Resource("personName")
@Handler(supports=PersonName.class, order=0)
public class PersonNameResource extends DataDelegatingCrudResource<PersonName> {

	public PersonNameResource() {
	    super(null);
    }

	public PersonNameResource(PersonName name) {
	    super(name);
    }
	
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep() throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("givenName");
		rep.addProperty("middleName");
		rep.addProperty("familyName");
		rep.addProperty("familyName2");
		return convertDelegateToRepresentation(rep);
	}


	@Override
	public PersonName fromString(String uuid) {
		return Context.getPersonService().getPersonNameByUuid(uuid);
	}
	
	@Override
	public void delete(String reason, RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub
	}
	
	@Override
	public void purge(RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub	
	}
	
	@Override
	protected PersonName saveDelegate() {
		// make sure that the name has actually been added to the person
		boolean needToAdd = true;
		for (PersonName pn : delegate.getPerson().getNames()) {
			if (pn.equals(delegate)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			delegate.getPerson().addName(delegate);
		Context.getPersonService().savePerson(delegate.getPerson());
		return delegate;
	}
	
	@Override
	protected PersonName newDelegate() {
		return new PersonName();
	}
	
}

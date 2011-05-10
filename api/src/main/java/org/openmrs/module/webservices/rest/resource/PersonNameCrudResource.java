package org.openmrs.module.webservices.rest.resource;

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;
import org.openmrs.module.webservices.rest.annotation.Resource;

@Resource("personName")
@Handler(supports=PersonName.class, order=0)
public class PersonNameCrudResource extends DataDelegatingCrudResource<PersonName> {

	public PersonNameCrudResource() {
	    super(null);
    }

	public PersonNameCrudResource(PersonName name) {
	    super(name);
    }
	
	@RepHandler(value=NamedRepresentation.class, name="default")
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
	public void delete(String reason, RequestContext context) throws ResourceDeletionException {
		// TODO Auto-generated function stub
	}
	
	@Override
	public void purge(RequestContext context) throws ResourceDeletionException {
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

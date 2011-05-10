package org.openmrs.module.webservices.rest.resource;

import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.annotation.IncludeProperties;
import org.openmrs.module.webservices.rest.annotation.Resource;

@Resource("personName")
@Handler(supports=PersonName.class)
@IncludeProperties(rep="default", properties={ "givenName", "middleName", "familyName", "familyName2" })
public class PersonNameCrudResource extends DataDelegatingCrudResource<PersonName> {

	public PersonNameCrudResource() {
	    super(null);
    }

	public PersonNameCrudResource(PersonName name) {
	    super(name);
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

package org.openmrs.module.webservices.rest.resource;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.NamedRepresentation;
import org.openmrs.module.webservices.rest.RefRepresentation;
import org.openmrs.module.webservices.rest.RequestContext;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.annotation.RepHandler;
import org.openmrs.module.webservices.rest.annotation.Resource;

/**
 * Resource for Encounters, supporting standard CRUD operations 
 */
@Resource("encounter")
@Handler(supports=Encounter.class, order=0)
public class EncounterCrudResource extends DataDelegatingCrudResource<Encounter> {

	public EncounterCrudResource() {
		super(null);
	}
	
	public EncounterCrudResource(Encounter delegate) {
		super(delegate);
	}
	
	@RepHandler(value=NamedRepresentation.class, name="default")
	public SimpleObject asDefaultRep() throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("encounterDatetime");
		rep.addProperty("patient", new RefRepresentation());
		rep.addProperty("location", new RefRepresentation());
		rep.addProperty("form", new RefRepresentation());
		rep.addProperty("encounterType", new RefRepresentation());
		rep.addProperty("provider", new RefRepresentation());
		rep.addMethodProperty("auditInfo", getClass().getMethod("getAuditInfo"));
		return convertDelegateToRepresentation(rep);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Encounter newDelegate() {
	    return new Encounter();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#saveDelegate()
	 */
	@Override
	public Encounter saveDelegate() {
	    return Context.getEncounterService().saveEncounter(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegateConverter#fromString(java.lang.String)
	 */
	@Override
	public Encounter fromString(String uuid) {
	    return Context.getEncounterService().getEncounterByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#delete(java.lang.String, org.openmrs.module.webservices.rest.RequestContext)
	 */
	@Override
	public void delete(String reason, RequestContext context) throws ResourceDeletionException {
	    Context.getEncounterService().voidEncounter(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#purge(RequestContext))
	 */
	@Override
	public void purge(RequestContext context) throws ResourceDeletionException {
		try {
			Context.getEncounterService().purgeEncounter(delegate);
		} catch (Exception ex) {
			throw new ResourceDeletionException(ex);
		}
	}

}

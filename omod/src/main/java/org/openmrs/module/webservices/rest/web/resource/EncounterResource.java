package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Resource for Encounters, supporting standard CRUD operations 
 */
@Resource("encounter")
@Handler(supports=Encounter.class, order=0)
public class EncounterResource extends DataDelegatingCrudResource<Encounter> {

	public EncounterResource() {
		super(null);
	}
	
	public EncounterResource(Encounter delegate) {
		super(delegate);
	}
	
	/**
	 * @return default representation of this resource 
	 */
	@RepHandler(DefaultRepresentation.class)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Encounter newDelegate() {
	    return new Encounter();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#saveDelegate()
	 */
	@Override
	public Encounter saveDelegate() {
	    return Context.getEncounterService().saveEncounter(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.DelegateConverter#fromString(java.lang.String)
	 */
	@Override
	public Encounter fromString(String uuid) {
	    return Context.getEncounterService().getEncounterByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
	    Context.getEncounterService().voidEncounter(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.resource.DelegatingCrudResource#purge(RequestContext))
	 */
	@Override
	public void purge(RequestContext context) throws ResponseException {
		if (delegate == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getEncounterService().purgeEncounter(delegate);
	}

}

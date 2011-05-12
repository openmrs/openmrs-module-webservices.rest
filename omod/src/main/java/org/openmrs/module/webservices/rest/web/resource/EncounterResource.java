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
	
	/**
	 * @return default representation of this resource 
	 */
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(Encounter enc) throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("encounterDatetime");
		rep.addProperty("patient", new RefRepresentation());
		rep.addProperty("location", new RefRepresentation());
		rep.addProperty("form", new RefRepresentation());
		rep.addProperty("encounterType", new RefRepresentation());
		rep.addProperty("provider", new RefRepresentation());
		rep.addMethodProperty("auditInfo", getClass().getMethod("getAuditInfo"));
		return convertDelegateToRepresentation(enc, rep);
	}
	
	@Override
	public Encounter newDelegate() {
	    return new Encounter();
	}
	
	@Override
	public Encounter save(Encounter enc) {
	    return Context.getEncounterService().saveEncounter(enc);
	}
	
	@Override
	public Encounter getByUniqueId(String uuid) {
	    return Context.getEncounterService().getEncounterByUuid(uuid);
	}
	
	@Override
	public void delete(Encounter enc, String reason, RequestContext context) throws ResponseException {
		if (enc.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
	    Context.getEncounterService().voidEncounter(enc, reason);
	}
	
	@Override
	public void purge(Encounter enc, RequestContext context) throws ResponseException {
		if (enc == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getEncounterService().purgeEncounter(enc);
	}

}

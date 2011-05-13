package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Encounter;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Resource for Encounters, supporting standard CRUD operations 
 */
@Resource("encounter")
@Handler(supports=Encounter.class, order=0)
public class EncounterResource extends DataDelegatingCrudResource<Encounter> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
	    if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("encounterDatetime");
			description.addProperty("patient", new RefRepresentation());
			description.addProperty("location", new RefRepresentation());
			description.addProperty("form", new RefRepresentation());
			description.addProperty("encounterType", new RefRepresentation());
			description.addProperty("provider", new RefRepresentation());
			description.addMethodProperty("auditInfo", findMethod("getAuditInfo"));
			return description;
	    }
	    return null;
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

package org.openmrs.module.webservices.rest.web.resource;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceRepresentation;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Patients, supporting standard CRUD operations, and listing and adding of the
 * "names" subresource
 */
@Resource("patient")
@Handler(supports=Patient.class, order=0)
public class PatientResource extends DataDelegatingCrudResource<Patient> {
		
	public PatientResource() {
		super(null);
	}
	
	public PatientResource(Patient patient) {
		super(patient);
	}
	
	/**
	 * @return default representation of this resource 
	 */
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep() throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("gender");
		rep.addProperty("age");
		rep.addProperty("birthdate");
		rep.addProperty("birthdateEstimated");
		rep.addProperty("dead");
		rep.addProperty("deathDate");
		rep.addProperty("causeOfDeath");
		rep.addProperty("personName", Representation.REF);
		rep.addProperty("personAddress", Representation.REF);
		rep.addProperty("activeIdentifiers", Representation.REF);
		rep.addProperty("activeAttributes", Representation.REF);
		return convertDelegateToRepresentation(rep);
	}
	
	/**
	 * @return full representation of this resource 
	 */
	@RepHandler(FullRepresentation.class)
	public SimpleObject asFullRep() throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("gender");
		rep.addProperty("age");
		rep.addProperty("birthdate");
		rep.addProperty("birthdateEstimated");
		rep.addProperty("dead");
		rep.addProperty("deathDate");
		rep.addProperty("causeOfDeath");
		rep.addProperty("personName", Representation.DEFAULT);
		rep.addProperty("personAddress", Representation.DEFAULT);
		rep.addProperty("names", Representation.DEFAULT);
		rep.addProperty("addresses", Representation.DEFAULT);
		rep.addProperty("identifiers", Representation.DEFAULT);
		rep.addProperty("attributes", Representation.DEFAULT);
		rep.addMethodProperty("auditInfo", getClass().getMethod("getAuditInfo"));
		return convertDelegateToRepresentation(rep);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
    public Patient newDelegate() {
	    return new Patient();
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#saveDelegate()
	 */
	@Override
    public Patient saveDelegate() {
	    return Context.getPatientService().savePatient(delegate);
    }

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.DelegateConverter#fromString(java.lang.String)
	 */
	@Override
	public Patient fromString(String uuid) {
	    return Context.getPatientService().getPatientByUuid(uuid);
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
	    Context.getPatientService().voidPatient(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(RequestContext context) throws ResponseException {
		if (delegate == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPatientService().purgePatient(delegate);
	}

	/**
	 * Lists current values for a given subresource
	 * @param subResourceName
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Object listSubResource(String subResourceName, RequestContext context) throws ResponseException {
		return getPropertyWithRepresentation(subResourceName, context.getRepresentation());
    }
	
	/**
	 * Adds a new PersonName to the "names" subresource
	 * @param post
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public PersonNameResource createPersonName(SimpleObject post, RequestContext context) throws ResponseException {
		post.put("person", delegate);
		return (PersonNameResource) new PersonNameResource().create(post, context);
	}
	
}

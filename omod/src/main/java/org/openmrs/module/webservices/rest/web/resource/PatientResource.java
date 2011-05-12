package org.openmrs.module.webservices.rest.web.resource;

import java.util.Arrays;
import java.util.List;

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
	
	/**
	 * @return default representation of this resource 
	 */
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(Patient patient) throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("gender");
		rep.addProperty("age");
		rep.addProperty("birthdate");
		rep.addProperty("birthdateEstimated");
		rep.addProperty("dead");
		rep.addProperty("deathDate");
		rep.addProperty("causeOfDeath", Representation.REF);
		rep.addProperty("personName", Representation.REF);
		rep.addProperty("personAddress", Representation.REF);
		rep.addProperty("activeIdentifiers", Representation.REF);
		rep.addProperty("activeAttributes", Representation.REF);
    	rep.addMethodProperty("uri", findMethod("getUri"));
		return convertDelegateToRepresentation(patient, rep);
	}
	
	/**
	 * @return full representation of this resource 
	 */
	@RepHandler(FullRepresentation.class)
	public SimpleObject asFullRep(Patient patient) throws Exception {
		DelegatingResourceRepresentation rep = new DelegatingResourceRepresentation();
		rep.addProperty("uuid");
		rep.addProperty("gender");
		rep.addProperty("age");
		rep.addProperty("birthdate");
		rep.addProperty("birthdateEstimated");
		rep.addProperty("dead");
		rep.addProperty("deathDate");
		rep.addProperty("causeOfDeath");
		rep.addProperty("personName");
		rep.addProperty("personAddress");
		rep.addProperty("names");
		rep.addProperty("addresses");
		rep.addProperty("identifiers");
		rep.addProperty("attributes");
		rep.addMethodProperty("auditInfo", findMethod("getAuditInfo"));
		rep.addMethodProperty("uri", findMethod("getUri"));
		return convertDelegateToRepresentation(patient, rep);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
    public Patient newDelegate() {
	    return new Patient();
    }
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
    public Patient save(Patient patient) {
	    return Context.getPatientService().savePatient(patient);
    }

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Patient getByUniqueId(String uuid) {
	    return Context.getPatientService().getPatientByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object, java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(Patient patient, String reason, RequestContext context) throws ResponseException {
		if (patient.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
	    Context.getPatientService().voidPatient(patient, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Patient patient, RequestContext context) throws ResponseException {
		if (patient == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getPatientService().purgePatient(patient);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#propertiesToExposeAsSubResources()
	 */
	@Override
	protected List<String> propertiesToExposeAsSubResources() {
	    return Arrays.asList("names");
	}

	/**
	 * Adds a new PersonName to the "names" subresource
	 * @param post
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public Object createPersonName(String uuid, SimpleObject post, RequestContext context) throws ResponseException {
		post.put("person", getByUniqueId(uuid));
		return new PersonNameResource().create(post, context);
	}
	
}

package org.openmrs.module.webservices.rest.web.resource;

import java.util.Arrays;
import java.util.List;

import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Patients, supporting standard CRUD operations, and listing and adding of the
 * "names" subresource
 */
@Resource("patient")
@Handler(supports=Patient.class, order=0)
public class PatientResource extends DataDelegatingCrudResource<Patient> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
	    if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath", Representation.REF);
			description.addProperty("personName", Representation.REF);
			description.addProperty("personAddress", Representation.REF);
			description.addProperty("activeIdentifiers", Representation.REF);
			description.addProperty("activeAttributes", Representation.REF);
	    	description.addMethodProperty("uri", findMethod("getUri"));
	    	return description;
	    } else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("gender");
			description.addProperty("age");
			description.addProperty("birthdate");
			description.addProperty("birthdateEstimated");
			description.addProperty("dead");
			description.addProperty("deathDate");
			description.addProperty("causeOfDeath");
			description.addProperty("personName");
			description.addProperty("personAddress");
			description.addProperty("names");
			description.addProperty("addresses");
			description.addProperty("identifiers");
			description.addProperty("attributes");
			description.addMethodProperty("auditInfo", findMethod("getAuditInfo"));
			description.addMethodProperty("uri", findMethod("getUri"));
			return description;
	    }
	    return null;
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
	
}

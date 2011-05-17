package org.openmrs.module.webservices.rest.web.resource;

import java.util.List;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Sub-resource for patient identifiers
 */
@SubResource(parent=PatientResource.class, parentProperty="REMOVE-THIS-PROPERTY", path="identifiers")
@Handler(supports=PatientIdentifier.class, order=0)
public class PatientIdentifierResource extends DelegatingSubResource<PatientIdentifier, Patient, PatientResource> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("identifier");
			description.addProperty("identifierType", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("preferred");
			description.addProperty("uri", findMethod("getUri"));
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("identifier");
			description.addProperty("identifierType", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("preferred");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			return description;
		}
		return null;
	}

	private PatientService service() {
		return Context.getPatientService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Patient getParent(PatientIdentifier instance) {
	    return instance.getPatient();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setParent(PatientIdentifier instance, Patient patient) {
	    instance.setPatient(patient);
	}
	
	@Override
	public PatientIdentifier getByUniqueId(String uniqueId) {
		return service().getPatientIdentifierByUuid(uniqueId);
	}
	
	@Override
	protected PatientIdentifier save(PatientIdentifier delegate) {
		// make sure it has already been added to the patient
		boolean needToAdd = true;
		for (PatientIdentifier pi : delegate.getPatient().getActiveIdentifiers()) {
			if (pi.equals(delegate)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			delegate.getPatient().addIdentifier(delegate);
		return service().savePatientIdentifier(delegate);
	}
	
	@Override
    public PatientIdentifier newDelegate() {
		return new PatientIdentifier();
	}
	
	@Override
	protected void delete(PatientIdentifier delegate, String reason, RequestContext context) throws ResponseException {
		service().voidPatientIdentifier(delegate, reason);
	}
	
	@Override
	public void purge(PatientIdentifier delegate, RequestContext context) throws ResponseException {
		service().purgePatientIdentifier(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public List<PatientIdentifier> doGetAll(Patient parent, RequestContext context) throws ResponseException {
	    return parent.getActiveIdentifiers();
	}
	
	/**
	 * @param id
	 * @return identifier type + identifier (for concise display purposes)
	 *
	*/
	public String getDisplayString(PatientIdentifier id) {
		return id.getIdentifierType().getName() + " = " + id.getIdentifier();
	}

}

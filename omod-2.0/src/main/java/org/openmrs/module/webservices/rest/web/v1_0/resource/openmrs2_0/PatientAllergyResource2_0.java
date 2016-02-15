package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Allergy;
import org.openmrs.Allergies;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.PatientResource1_9;

@SubResource(parent = PatientResource1_9.class, path = "allergy", supportedClass = Allergy.class, supportedOpenmrsVersions = {
        "2.0.*", "2.1.*" })
public class PatientAllergyResource2_0 extends DelegatingSubResource<Allergy, Patient, PatientResource1_9> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			// what should be in default representation
			description.addProperty("display");
			description.addProperty("uuid"); 
			description.addProperty("allergen", rep);
			description.addProperty("severity", rep);
			description.addProperty("comment", rep);
			description.addProperty("reactions", rep);
			description.addProperty("patient", rep);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			// what should be in full representation
			description.addProperty("display");
			description.addProperty("uuid"); 
			description.addProperty("allergen", rep);
			description.addProperty("severity", rep);
			description.addProperty("comment", rep);
			description.addProperty("reactions", rep);
			description.addProperty("patient", rep);
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("allergen");
		description.addProperty("severity");
		description.addProperty("comment");
		description.addProperty("reactions");
        return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Patient getParent(Allergy instance) {
		return instance.getPatient();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(Allergy instance, Patient patient) {
		instance.setPatient(patient);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<Allergy> doGetAll(Patient parent, RequestContext context) throws ResponseException {
		List<Allergy> allergiesList = new ArrayList<Allergy>();
		Allergies allergies = new Allergies();
		if (parent != null) {
			allergies = Context.getPatientService().getAllergies(parent);
			if (allergies.getAllergyStatus().equals(Allergies.UNKNOWN)) {
				// return 404 status for a patient whoose allergy status is unknown
				throw new ObjectNotFoundException();
			} else if (allergies.getAllergyStatus().equals(Allergies.NO_KNOWN_ALLERGIES)) {
				// empty list
			} else if (allergies.getAllergyStatus().equals(Allergies.SEE_LIST)) {
				// return list
				allergiesList.addAll(allergies);
			}
		}
		return new NeedsPaging<Allergy>(allergiesList, context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Allergy getByUniqueId(String uuid) {
		return Context.getPatientService().getAllergyByUuid(uuid);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(Allergy allergy, String reason, RequestContext context) throws ResponseException {
		Context.getPatientService().voidAllergy(allergy, reason);
	}
	
	@Override
	public void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException {
		if (parentUniqueId != null && uuid != null) {
			// DELETE /patient/UUID/allergy/ALLERGYUUID means to delete one allergy from patient
			Allergy allergy = getByUniqueId(uuid);
			Context.getPatientService().voidAllergy(allergy, reason);
		} else if (parentUniqueId != null && uuid == null) {
			// DELETE /patient/UUID/allergy/ means to delete all allergies from patient, back to Unknown Allergies state
			Patient patient = Context.getPatientService().getPatientByUuid(parentUniqueId);
			Allergies allergies = Context.getPatientService().getAllergies(patient);
			for (Allergy allergy : allergies) {
				Context.getPatientService().voidAllergy(allergy, reason);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Allergy newAllergy, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	@Override
	public void purge(String parentUniqueId, String uuid, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Allergy save(Allergy newAllergy) {
		Context.getPatientService().saveAllergy(newAllergy);
		return newAllergy;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Allergy newDelegate() {
		return new Allergy();
	}

	/**
	 * Gets the display string for a person name.
	 *
	 * @param Allergy the allergy object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(Allergy allergy) {
		if (allergy.getAllergen().getCodedAllergen() != null) {
			return allergy.getAllergen().getCodedAllergen().getDisplayString();
        } else {
			return allergy.getAllergen().getNonCodedAllergen();
        }
	}
}

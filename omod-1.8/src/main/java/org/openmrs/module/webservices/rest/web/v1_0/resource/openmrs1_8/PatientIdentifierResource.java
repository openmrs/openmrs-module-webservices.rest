/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Sub-resource for patient identifiers
 */
@SubResource(parent = PatientResource.class, path = "identifier", supportedClass = PatientIdentifier.class, supportedOpenmrsVersions = {"1.8.*", "1.9.*"})
public class PatientIdentifierResource extends DelegatingSubResource<PatientIdentifier, Patient, PatientResource> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("uuid");
			description.addProperty("identifier");
			description.addProperty("identifierType", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("preferred");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("uuid");
			description.addProperty("identifier");
			description.addProperty("identifierType", Representation.DEFAULT);
			description.addProperty("location", Representation.DEFAULT);
			description.addProperty("preferred");
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
		description.addRequiredProperty("identifier");
		description.addRequiredProperty("identifierType");
		description.addProperty("location");
		description.addProperty("preferred");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(PatientIdentifier instance, Patient patient) {
		instance.setPatient(patient);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public PatientIdentifier getByUniqueId(String uniqueId) {
		return service().getPatientIdentifierByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public PatientIdentifier save(PatientIdentifier delegate) {
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
		Patient patient = delegate.getPatient();
		patient.removeIdentifier(delegate);
		service().savePatient(patient);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<PatientIdentifier> doGetAll(Patient parent, RequestContext context) throws ResponseException {
		return new NeedsPaging<PatientIdentifier>(parent.getActiveIdentifiers(), context);
	}
	
	/**
	 * @param id
	 * @return identifier type + identifier (for concise display purposes)
	 */
	public String getDisplayString(PatientIdentifier id) {
		if (id.getIdentifierType() == null)
			return "";
		
		return id.getIdentifierType().getName() + " = " + id.getIdentifier();
	}
}

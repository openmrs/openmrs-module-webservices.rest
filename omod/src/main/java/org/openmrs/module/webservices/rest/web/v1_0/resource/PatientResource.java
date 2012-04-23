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
package org.openmrs.module.webservices.rest.web.v1_0.resource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.Person;
import org.openmrs.annotation.Handler;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.ServiceSearcher;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for Patients, supporting standard CRUD operations
 */
@Resource("patient")
@Handler(supports = Patient.class, order = 1)
public class PatientResource extends DataDelegatingCrudResource<Patient> {
	
	public PatientResource() {
	}
	
	@PropertyGetter("person")
	public static Person getPerson(Patient instance) {
		return instance;
	}
	
	/**
	 * It is empty, because we set that already in the create method.
	 * <p>
	 * It takes String instead of Person so that the uuid is not resolved to a person, which
	 * leads to the Hibernate exception: the object is already associated with the session.
	 * 
	 * @param instance
	 * @param personUuid
	 */
	@PropertySetter("person")
	public static void setPerson(Patient instance, String personUuid) {
	}
	
	@PropertyGetter("identifiers")
	public static Set<PatientIdentifier> getIdentifiers(Patient instance) {
		return new HashSet<PatientIdentifier>(instance.getActiveIdentifiers());
	}
	
	@PropertySetter("identifiers")
	public static void setPerson(Patient instance, Set<PatientIdentifier> identifiers) {
		instance.getIdentifiers().clear();
		instance.getIdentifiers().addAll(identifiers);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("identifiers", Representation.REF);
			description.addProperty("person", Representation.DEFAULT);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("identifiers", Representation.DEFAULT);
			description.addProperty("person", Representation.FULL);
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
	public DelegatingResourceDescription getCreatableProperties() throws ResponseException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("person");
		description.addRequiredProperty("identifiers");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResponseException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("identifiers");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#create(org.openmrs.module.webservices.rest.SimpleObject, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		//We need to create a patient from an existing person.
		Object object = propertiesToCreate.get("person");
		if (object == null) {
			throw new ConversionException("The person property is missing");
		}
		Person person = Context.getPersonService().getPersonByUuid((String) object);
		
		Patient delegate = new Patient(person);
		
		Context.evictFromSession(person);
		
		setConvertedProperties(delegate, propertiesToCreate, getCreatableProperties(), true);
		delegate = save(delegate);
		return ConversionUtil.convertToRepresentation(delegate, Representation.DEFAULT);
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected AlreadyPaged<Patient> doSearch(String query, RequestContext context) {
		return new ServiceSearcher<Patient>(PatientService.class, "getPatients", "getCountOfPatients")
		        .search(query, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#propertiesToExposeAsSubResources()
	 */
	@Override
	protected List<String> propertiesToExposeAsSubResources() {
		return Arrays.asList("identifiers");
	}
	
	/**
	 * @param patient
	 * @return identifier + name (for concise display purposes)
	 */
	public String getDisplayString(Patient patient) {
		if (patient.getPatientIdentifier() == null)
			return "";
		
		return patient.getPatientIdentifier().getIdentifier() + " - " + patient.getPersonName().getFullName();
	}
	
}

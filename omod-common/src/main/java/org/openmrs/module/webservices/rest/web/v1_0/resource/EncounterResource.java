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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.EncounterService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.resource.impl.ServiceSearcher;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Resource for Encounters, supporting standard CRUD operations 
 */
@Resource(name = "encounter", supportedClass = Encounter.class)
public class EncounterResource extends DataDelegatingCrudResource<Encounter> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("encounterDatetime");
			description.addProperty("patient", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("form", Representation.REF);
			description.addProperty("encounterType", Representation.REF);
			description.addProperty("provider", Representation.REF);
			description.addProperty("obs", Representation.REF);
			description.addProperty("orders", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("encounterDatetime");
			description.addProperty("patient", Representation.REF);
			description.addProperty("location");
			description.addProperty("form");
			description.addProperty("encounterType");
			description.addProperty("provider");
			description.addProperty("obs");
			description.addProperty("orders");
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 * @should create an encounter type
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addRequiredProperty("encounterDatetime");
		description.addRequiredProperty("patient");
		description.addRequiredProperty("encounterType");
		
		description.addProperty("location");
		description.addProperty("form");
		description.addProperty("provider");
		description.addProperty("orders");
		description.addProperty("obs");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Encounter newDelegate() {
		Encounter enc = new Encounter();
		// As of 2012-04-27 there is a bug in Encounter.getOrders() where, if null, it returns an empty list without keeping a reference to it
		enc.setOrders(new LinkedHashSet<Order>());
		return enc;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#save(org.openmrs.Encounter)
	 */
	@Override
	public Encounter save(Encounter enc) {
		return Context.getEncounterService().saveEncounter(enc);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Encounter getByUniqueId(String uuid) {
		return Context.getEncounterService().getEncounterByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(org.openmrs.Encounter, 
	 * java.lang.String, 
	 * org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(Encounter enc, String reason, RequestContext context) throws ResponseException {
		if (enc.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getEncounterService().voidEncounter(enc, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(org.openmrs.Encounter,
	 * org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Encounter enc, RequestContext context) throws ResponseException {
		if (enc == null) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getEncounterService().purgeEncounter(enc);
	}
	
	/**
	 * @param encounter
	 * @return encounter type and date
	 */
	public String getDisplayString(Encounter encounter) {
		String ret = encounter.getEncounterType() == null ? "?" : encounter.getEncounterType().getName();
		ret += " ";
		ret += encounter.getEncounterDatetime() == null ? "?" : Context.getDateFormat().format(
		    encounter.getEncounterDatetime());
		return ret;
	}
	
	/**
	 * Gets encounters for the given patient (paged according to context if necessary)
	 * 
	 * @param patientUniqueId @see {@link PatientResource#getByUniqueId(String)} for interpretation
	 * @param context
	 * @return
	 * @throws ResponseException 
	 */
	public SimpleObject getEncountersByPatient(String patientUniqueId, RequestContext context) throws ResponseException {
		Patient patient = ((PatientResource) Context.getService(RestService.class)
		        .getResourceBySupportedClass(Patient.class)).getByUniqueId(patientUniqueId);
		if (patient == null)
			throw new ObjectNotFoundException();
		List<Encounter> encs = Context.getEncounterService().getEncountersByPatient(patient);
		return new NeedsPaging<Encounter>(encs, context).toSimpleObject();
	}
	
	/**
	 * @param instance
	 * @return all non-voided top-level obs from the given encounter
	 */
	@PropertyGetter("obs")
	public static Object getObsAtTopLevel(Encounter instance) {
		return instance.getObsAtTopLevel(false);
	}
	
	@PropertySetter("obs")
	public static void setObs(Encounter instance, Set<Obs> obs) {
		for (Obs o : obs)
			instance.addObs(o);
	}
	
	@PropertySetter("order")
	public static void setOrders(Encounter instance, Set<Order> orders) {
		for (Order o : orders)
			instance.addOrder(o);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected AlreadyPaged<Encounter> doSearch(String query, RequestContext context) {
		return new ServiceSearcher<Encounter>(EncounterService.class, "getEncounters", "getCountOfEncounters").search(query,
		    context);
	}
	
}

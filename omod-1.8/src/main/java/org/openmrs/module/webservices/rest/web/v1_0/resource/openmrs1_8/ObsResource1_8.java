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

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptNumeric;
import org.openmrs.Encounter;
import org.openmrs.Obs;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * {@link Resource} for Obs, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/obs", order = 2, supportedClass = Obs.class, supportedOpenmrsVersions = {"1.8.*"})
public class ObsResource1_8 extends DataDelegatingCrudResource<Obs> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(Obs delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getObsService().voidObs(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Obs getByUniqueId(String uniqueId) {
		return Context.getObsService().getObsByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			// TODO how to handle valueCodedName?
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept", Representation.REF);
			description.addProperty("person", Representation.REF);
			description.addProperty("obsDatetime");
			description.addProperty("accessionNumber");
			description.addProperty("obsGroup", Representation.REF);
			description.addProperty("valueCodedName", Representation.REF);
			description.addProperty("groupMembers");
			description.addProperty("comment");
			description.addProperty("location", Representation.REF);
			description.addProperty("order", Representation.REF);
			description.addProperty("encounter", Representation.REF);
			description.addProperty("voided");
			description.addProperty("value");
			description.addProperty("valueModifier");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			// TODO how to handle valueCodedName?
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("concept");
			description.addProperty("person", Representation.REF);
			description.addProperty("obsDatetime");
			description.addProperty("accessionNumber");
			description.addProperty("obsGroup");
			description.addProperty("valueCodedName");
			description.addProperty("groupMembers", Representation.FULL);
			description.addProperty("comment");
			description.addProperty("location");
			description.addProperty("order");
			description.addProperty("encounter");
			description.addProperty("voided");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addProperty("value");
			description.addProperty("valueModifier");
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
		
		description.addRequiredProperty("person");
		description.addRequiredProperty("obsDatetime");
		description.addRequiredProperty("concept");
		
		description.addProperty("location");
		description.addProperty("order");
		description.addProperty("encounter");
		description.addProperty("accessionNumber");
		description.addProperty("groupMembers");
		description.addProperty("valueCodedName");
		description.addProperty("comment");
		description.addProperty("value");
		description.addProperty("valueModifier");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Obs newDelegate() {
		return new Obs();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Obs delegate, RequestContext context) throws ResponseException {
		Context.getObsService().purgeObs(delegate);
		
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Obs save(Obs delegate) {
		return Context.getObsService().saveObs(delegate, "REST web service");
	}
	
	/**
	 * Display string for Obs
	 * 
	 * @param obs
	 * @return String ConceptName = value
	 */
	@PropertyGetter("display")
	public String getDisplayString(Obs obs) {
		if (obs.getConcept() == null)
			return "";
		
		return obs.getConcept().getName() + ": " + obs.getValueAsString(Context.getLocale());
	}
	
	/**
	 * Retrives the Obs Value as string
	 * 
	 * @param obs
	 * @return
	 */
	@PropertyGetter("value")
	public static Object getValue(Obs obs) throws ConversionException {
		if (obs.isObsGrouping())
			return null;
		
		if (obs.getValueDatetime() != null)
			return ConversionUtil.convert(obs.getValueDatetime(), Date.class);
		
		if (obs.getValueCoded() != null)
			return obs.getValueCoded();
		
		if (obs.getValueComplex() != null)
			return obs.getValueComplex();
		
		if (obs.getValueDrug() != null)
			return obs.getValueDrug();
		
		if (obs.getValueText() != null)
			return obs.getValueText();
		
		if (obs.getValueNumeric() != null)
			return obs.getValueNumeric();
		
		return null;
	}

	/**
	 * Sets the members of an obs group
	 *
	 * @param obsGroup the obs group whose members to set
	 * @param members the members to set
	 */
	@PropertySetter("groupMembers")
	public static void setGroupMembers(Obs obsGroup, Set<Obs> members) {
		for (Obs member : members) {
			member.setObsGroup(obsGroup);
			member.setGroupMembers(Collections.<Obs>emptySet());
		}
		obsGroup.setGroupMembers(members);
	}

	/**
	 * Checks if there are more than one obs in GroupMembers and converts into a DEFAULT
	 * representation
	 * 
	 * @param obs
	 * @return Object
	 * @throws ConversionException
	 */
	@PropertyGetter("groupMembers")
	public static Object getGroupMembers(Obs obs) throws ConversionException {
		if (obs.getGroupMembers() != null && obs.getGroupMembers().size() > 0) {
			return obs.getGroupMembers();
		}
		return null;
	}

	/**
	 * Annotated setter for Concept
	 *
	 * @param obs
	 * @param value
	 */
	@PropertySetter("concept")
	public static void setConcept(Obs obs, Object value) {
		obs.setConcept(Context.getConceptService().getConceptByUuid((String) value));
	}

	/**
	 * Annotated setter for ConceptValue
	 * 
	 * @param obs
	 * @param value
	 * @throws ParseException
	 * @throws ConversionException
	 */
	@PropertySetter("value")
	public static void setValue(Obs obs, Object value) throws ParseException, ConversionException {
		if (value != null) {
			if (obs.getConcept().getDatatype().isCoded()) {
				// setValueAsString is not implemented for coded obs (in core)
				Concept valueCoded = (Concept) ConversionUtil.convert(value, Concept.class);
				obs.setValueCoded(valueCoded);
			} else {
				if (obs.getConcept().isNumeric()) {
					//get the actual persistent object rather than the hibernate proxy
					ConceptNumeric concept = Context.getConceptService().getConceptNumeric(obs.getConcept().getId());
					String units = concept.getUnits();
					if (StringUtils.isNotBlank(units)) {
						String originalValue = value.toString().trim();
						if (originalValue.endsWith(units))
							value = originalValue.substring(0, originalValue.indexOf(units)).trim();
						else {
							//check that that this value has no invalid units
							try {
								Double.parseDouble(originalValue);
							}
							catch (NumberFormatException e) {
								throw new APIException(originalValue + " has invalid units", e);
							}
						}
					}
				}
				
				obs.setValueAsString(value.toString());
			}
		} else
			throw new APIException("The value for an observation cannot be null");
	}
	
	/**
	 * Gets obs by patient or encounter (paged according to context if necessary) only if a patient
	 * or encounter parameter exists respectively in the request set on the {@link RequestContext}
	 * otherwise searches for obs that match the specified query
	 * 
	 * @param context
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String patientUuid = context.getRequest().getParameter("patient");
		if (patientUuid != null) {
			Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Patient.class)).getByUniqueId(patientUuid);
			if (patient == null)
				return new EmptySearchResult();
			List<Obs> obs = Context.getObsService().getObservationsByPerson(patient);
			return new NeedsPaging<Obs>(obs, context);
		}
		
		String encounterUuid = context.getRequest().getParameter("encounter");
		if (encounterUuid != null) {
			Encounter enc = ((EncounterResource1_8) Context.getService(RestService.class).getResourceBySupportedClass(
			    Encounter.class)).getByUniqueId(encounterUuid);
			if (enc == null)
				return new EmptySearchResult();
			List<Obs> obs = new ArrayList<Obs>(enc.getAllObs());
			return new NeedsPaging<Obs>(obs, context);
		}
		
		return new NeedsPaging<Obs>(Context.getObsService().getObservations(context.getParameter("q")), context);
	}

}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.Concept;
import org.openmrs.Location;
import org.openmrs.Patient;
import org.openmrs.Encounter;
import org.openmrs.Visit;
import org.openmrs.VisitType;
import org.openmrs.VisitAttribute;
import org.openmrs.api.context.Context;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.webservices.rest.SimpleObject;
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
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.LocationResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.PatientResource1_8;

/**
 * {@link Resource} for {@link Visit}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/visit", supportedClass = Visit.class, supportedOpenmrsVersions = { "1.9.* - 9.*" })
public class VisitResource1_9 extends DataDelegatingCrudResource<Visit> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("visitType", Representation.REF);
			description.addProperty("indication", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("startDatetime");
			description.addProperty("stopDatetime");
			description.addProperty("encounters", Representation.REF);
			description.addProperty("attributes", Representation.REF);
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("patient", Representation.REF);
			description.addProperty("visitType", Representation.REF);
			description.addProperty("indication", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("startDatetime");
			description.addProperty("stopDatetime");
			description.addProperty("encounters", Representation.DEFAULT);
			description.addProperty("attributes", Representation.DEFAULT);
			description.addProperty("voided");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * Returns a display string
	 * 
	 * @param visit
	 * @return the display string
	 */
	@PropertyGetter("display")
	public String getDisplayString(Visit visit) {
		String ret = visit.getVisitType() == null ? "?" : visit.getVisitType().getName();
		ret += " ";
		ret += visit.getLocation() == null ? "?" : "@ " + visit.getLocation().getName();
		ret += " - ";
		ret += visit.getStartDatetime() == null ? "?" : Context.getDateTimeFormat().format(visit.getStartDatetime());
		return ret;
	}
	
	@PropertyGetter("attributes")
	public Collection<VisitAttribute> getActiveAttributes(Visit visit) {
		return visit.getActiveAttributes();
	}
	
	@PropertyGetter("encounters")
	public Collection<Encounter> getNonVoidedEncounters(Visit visit) {
		Set<Encounter> allEncounters = visit.getEncounters();
		if (allEncounters != null && !allEncounters.isEmpty()) {
			Iterator<Encounter> encounterIterator = allEncounters.iterator();
			while (encounterIterator.hasNext()) {
				Encounter next = encounterIterator.next();
				if (next.isVoided()) {
					encounterIterator.remove();
				}
			}
		}
		return allEncounters;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("patient");
		description.addRequiredProperty("visitType");
		// RESTWS-488: startDatetime property to be non-mandatory
		description.addProperty("startDatetime");
		description.addProperty("location");
		description.addProperty("indication");
		description.addProperty("stopDatetime");
		description.addProperty("encounters");
		description.addProperty("attributes");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		//shouldn't be editing the patient
		description.removeProperty("patient");
		return description;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema instanceof Schema && (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation)) {
            schema
					.addProperty("uuid", new StringSchema())
					.addProperty("display", new StringSchema())
					.addProperty("startDatetime", new DateTimeSchema())
					.addProperty("stopDatetime", new DateTimeSchema())
					.addProperty("attributes", new ArraySchema().items(new StringSchema())) //FIXME type
					.addProperty("voided", new BooleanSchema());

			if (rep instanceof DefaultRepresentation) {
				schema
						.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientGetRef"))
						.addProperty("visitType", new Schema<VisitType>().$ref("#/components/schemas/VisittypeGetRef"))
						.addProperty("indication", new Schema<Concept>().$ref("#/components/schemas/ConceptGetRef"))
						.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationGetRef"))
						.addProperty("encounters", new ArraySchema().items(new Schema<Encounter>().$ref("#/components/schemas/EncounterGetRef")));
			} else if (rep instanceof FullRepresentation) {
				schema
						.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientGet"))
						.addProperty("visitType", new Schema<VisitType>().$ref("#/components/schemas/VisittypeGet"))
						.addProperty("indication", new Schema<Concept>().$ref("#/components/schemas/ConceptGet"))
						.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationGet"))
						.addProperty("encounters", new ArraySchema().items(new Schema<Encounter>().$ref("#/components/schemas/EncounterGet")));
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> schema = new ObjectSchema()
				.addProperty("patient", new StringSchema().example("uuid"))
				.addProperty("visitType", new StringSchema().example("uuid"))
				.addProperty("startDatetime", new DateTimeSchema())
				.addProperty("location", new StringSchema().example("uuid"))
				.addProperty("indication", new StringSchema())
				.addProperty("stopDatetime", new DateTimeSchema())
				.addProperty("encounters", new ArraySchema().items(new StringSchema().example("uuid")))
				.addProperty("attributes", new ArraySchema().items(new Schema<VisitAttribute>().$ref("#/components/schemas/VisitAttributeCreate")));

		schema.setRequired(Arrays.asList("patient", "visitType"));

		if (rep instanceof FullRepresentation) {
			schema
					.addProperty("patient", new Schema<Patient>().$ref("#/components/schemas/PatientCreate"))
					.addProperty("visitType", new Schema<VisitType>().$ref("#/components/schemas/VisittypeCreate"))
					.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationCreate"))
					.addProperty("indication", new Schema<Concept>().$ref("#/components/schemas/ConceptCreate"))
					.addProperty("encounters", new ArraySchema().items(new Schema<Encounter>().$ref("#/components/schemas/EncounterCreate")));
		}
		return schema;
	}

	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return new ObjectSchema()
				.addProperty("visitType", new Schema<VisitType>().$ref("#/components/schemas/VisittypeCreate"))
				.addProperty("startDatetime", new DateTimeSchema())
				.addProperty("location", new Schema<Location>().$ref("#/components/schemas/LocationCreate"))
				.addProperty("indication", new Schema<Concept>().$ref("#/components/schemas/ConceptCreate"))
				.addProperty("stopDatetime", new DateTimeSchema())
				.addProperty("encounters", new ArraySchema().items(new Schema<Encounter>().$ref("#/components/schemas/EncounterCreate")))
				.addProperty("attributes", new ArraySchema().items(new StringSchema())); //FIXME type
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Visit newDelegate() {
		return new Visit();
	}
	
	@Override
	public Object create(SimpleObject propertiesToCreate, RequestContext context) throws ResponseException {
		// RESTWS-488: set startDatetime if not provided
		if (propertiesToCreate.get("startDatetime") == null) {
			propertiesToCreate.add("startDatetime", new Date());
		}
		return super.create(propertiesToCreate, context);
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public Visit save(Visit visit) {
		return Context.getVisitService().saveVisit(visit);
	}
	
	/**
	 * Fetches a visit by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Visit getByUniqueId(String uuid) {
		return Context.getVisitService().getVisitByUuid(uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(org.openmrs.Encounter,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(Visit visit, String reason, RequestContext context) throws ResponseException {
		if (visit.isVoided()) {
			// Makes no sense, so we return success here
			return;
		}
		Context.getVisitService().voidVisit(visit, reason);
	}
	
	@Override
	protected Visit undelete(Visit visit, RequestContext context) throws ResponseException {
		if (visit.isVoided()) {
			visit = Context.getVisitService().unvoidVisit(visit);
		}
		return visit;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Visit visit, RequestContext context) throws ResponseException {
		if (visit == null)
			return;
		Context.getVisitService().purgeVisit(visit);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * Sets the attributes of a visit
	 * 
	 * @param visit the visit whose attributes to set
	 * @param attributes the attributes to set
	 */
	@PropertySetter("attributes")
	public static void setAttributes(Visit visit, Set<VisitAttribute> attributes) {
		for (VisitAttribute attribute : attributes) {
			visit.addAttribute(attribute);
			//We need to force the API to set valueReference on all new attributes before any hibernate
			//auto flushes happen otherwise if they happen before we do so, the attributes get rejected
			//in the DB since valueReference would still be null yet it is not nullable
			if (attribute.getId() == null) {
				CustomDatatypeUtil.saveIfDirty(attribute);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#search(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String patientParameter = context.getRequest().getParameter("patient");
		String locationParameter = context.getRequest().getParameter("location");
		String includeInactiveParameter = context.getRequest().getParameter("includeInactive");
		String fromStartDate = context.getRequest().getParameter("fromStartDate");
		String visitTypeParameter = context.getRequest().getParameter("visitType");
		String includeParentLocations = context.getRequest().getParameter("includeParentLocations");
		if (patientParameter != null || includeInactiveParameter != null || locationParameter != null
		        || visitTypeParameter != null) {
			Date minStartDate = fromStartDate != null ? (Date) ConversionUtil.convert(fromStartDate, Date.class) : null;
			return getVisits(context, patientParameter, includeInactiveParameter, minStartDate, locationParameter,
			    visitTypeParameter, includeParentLocations);
		} else {
			return super.search(context);
		}
	}
	
	private SimpleObject getVisits(RequestContext context, String patientParameter, String includeInactiveParameter,
	        Date minStartDate, String locationParameter, String visitTypeParameter, String includeParentLocations) {
		Collection<Patient> patients = patientParameter == null ? null : Arrays.asList(getPatient(patientParameter));
		Collection<Location> locations = locationParameter == null ? null :
				Boolean.parseBoolean(includeParentLocations) ?
						getLocationAndParents(getLocation(locationParameter), null) :
				Arrays.asList(getLocation(locationParameter));
		Collection<VisitType> visitTypes = visitTypeParameter == null ? null : Arrays
		        .asList(getVisitType(visitTypeParameter));
		boolean includeInactive = includeInactiveParameter == null ? true : Boolean.parseBoolean(includeInactiveParameter);
		return new NeedsPaging<Visit>(Context.getVisitService().getVisits(visitTypes, patients, locations, null,
		    minStartDate,
		    null, null, null, null, includeInactive, context.getIncludeAll()), context).toSimpleObject(this);
	}

	/**
	 * Recursively builds a list that includes the passed-in location and all it's ancestors
	 */
	private List<Location> getLocationAndParents(Location location, List<Location> locations) {
		if (locations == null) {
			locations = new ArrayList<Location>();
		}
		locations.add(location);
		if (location.getParentLocation() != null) {
			locations = getLocationAndParents(location.getParentLocation(), locations);
		}
		return locations;
	}

	/**
	 * Get all the visits
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Visit> doGetAll(RequestContext context) {
		return new NeedsPaging<Visit>(Context.getVisitService().getAllVisits(), context);
	}
	
	private Patient getPatient(String patientUniqueId) {
		Patient patient = ((PatientResource1_8) Context.getService(RestService.class).getResourceByName(
		    RestConstants.VERSION_1 + "/patient")).getByUniqueId(patientUniqueId);
		if (patient == null)
			throw new ObjectNotFoundException();
		return patient;
	}
	
	private Location getLocation(String locationUniqueId) {
		Location location = ((LocationResource1_8) Context.getService(RestService.class).getResourceByName(
		    RestConstants.VERSION_1 + "/location")).getByUniqueId(locationUniqueId);
		if (location == null)
			throw new ObjectNotFoundException();
		return location;
	}
	
	private VisitType getVisitType(String visitTypeUuid) {
		VisitType visitType = Context.getVisitService().getVisitTypeByUuid(visitTypeUuid);
		if (visitType == null)
			throw new ObjectNotFoundException();
		return visitType;
	}
}

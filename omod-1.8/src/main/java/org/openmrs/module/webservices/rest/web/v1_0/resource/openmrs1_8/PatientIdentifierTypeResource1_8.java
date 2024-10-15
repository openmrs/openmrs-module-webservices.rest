/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.PatientIdentifierType;
import org.openmrs.api.PatientService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;

/**
 * Allows standard CRUD for the {@link PatientIdentifierType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/patientidentifiertype", supportedClass = PatientIdentifierType.class, supportedOpenmrsVersions = {
        "1.8.* - 1.12.*" })
public class PatientIdentifierTypeResource1_8 extends MetadataDelegatingCrudResource<PatientIdentifierType> {
	
	public PatientIdentifierTypeResource1_8() {
		allowedMissingProperties.add("locationBehavior");
		allowedMissingProperties.add("uniquenessBehavior");
	}
	
	private PatientService service() {
		return Context.getPatientService();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("format");
			description.addProperty("formatDescription");
			description.addProperty("required");
			description.addProperty("checkDigit");
			description.addProperty("validator");
			description.addProperty("locationBehavior");
			description.addProperty("uniquenessBehavior");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("format");
			description.addProperty("formatDescription");
			description.addProperty("required");
			description.addProperty("checkDigit");
			description.addProperty("validator");
			description.addProperty("locationBehavior");
			description.addProperty("uniquenessBehavior");
			description.addProperty("validator"); //FIXME duplicate
			description.addProperty("retired");
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
		description.addRequiredProperty("name");
		description.addRequiredProperty("description");
		description.addProperty("format");
		description.addProperty("formatDescription");
		description.addProperty("required");
		description.addProperty("checkDigit");
		description.addProperty("validator");
		description.addProperty("locationBehavior");
		description.addProperty("uniquenessBehavior");
		description.addProperty("validator");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> model = super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			model
			        .addProperty("format", new StringSchema())
			        .addProperty("formatDescription", new StringSchema())
			        .addProperty("required", new BooleanSchema())
			        .addProperty("checkDigit", new BooleanSchema())
			        .addProperty("validator", new StringSchema())
					.addProperty("locationBehavior", new Schema<PatientIdentifierType.LocationBehavior>()._enum(Arrays.asList(PatientIdentifierType.LocationBehavior.values())))
			        .addProperty("uniquenessBehavior", new StringSchema()); //FIXME check type
		}
		return model;
	}
	
	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		return super.getCREATESchema(rep)
		        .addProperty("format", new StringSchema())
		        .addProperty("formatDescription", new StringSchema())
		        .addProperty("required", new BooleanSchema())
		        .addProperty("checkDigit", new BooleanSchema())
		        .addProperty("validator", new StringSchema())
		        .addProperty("locationBehavior", new Schema<PatientIdentifierType.LocationBehavior>()._enum(Arrays.asList(PatientIdentifierType.LocationBehavior.values())))
		        .addProperty("uniquenessBehavior", new StringSchema()); //FIXME check type
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return getCREATESchema(rep);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public PatientIdentifierType getByUniqueId(String uniqueId) {
		PatientIdentifierType type = service().getPatientIdentifierTypeByUuid(uniqueId);
		if (type == null)
			type = service().getPatientIdentifierTypeByName(uniqueId);
		return type;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<PatientIdentifierType> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<PatientIdentifierType>(service().getAllPatientIdentifierTypes(context.getIncludeAll()),
		        context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public PatientIdentifierType newDelegate() {
		return new PatientIdentifierType();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public PatientIdentifierType save(PatientIdentifierType delegate) {
		return service().savePatientIdentifierType(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(PatientIdentifierType delegate, RequestContext context) throws ResponseException {
		service().purgePatientIdentifierType(delegate);
	}
	
}

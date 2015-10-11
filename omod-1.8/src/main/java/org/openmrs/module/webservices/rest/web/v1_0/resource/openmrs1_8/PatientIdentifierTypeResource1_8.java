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

/**
 * Allows standard CRUD for the {@link PatientIdentifierType} domain object
 */
@Resource(name = RestConstants.VERSION_1 + "/patientidentifiertype", order = 200, supportedClass = PatientIdentifierType.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PatientIdentifierTypeResource1_8 extends
		MetadataDelegatingCrudResource<PatientIdentifierType> {

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
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
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
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
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
			description.addProperty("validator");
			description.addProperty("retired");
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

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public PatientIdentifierType getByUniqueId(String uniqueId) {
		PatientIdentifierType type = service().getPatientIdentifierTypeByUuid(
				uniqueId);
		if (type == null)
			type = service().getPatientIdentifierTypeByName(uniqueId);
		return type;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<PatientIdentifierType> doGetAll(RequestContext context)
			throws ResponseException {
		return new NeedsPaging<PatientIdentifierType>(service()
				.getAllPatientIdentifierTypes(context.getIncludeAll()), context);
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
	public void purge(PatientIdentifierType delegate, RequestContext context)
			throws ResponseException {
		service().purgePatientIdentifierType(delegate);
	}

}

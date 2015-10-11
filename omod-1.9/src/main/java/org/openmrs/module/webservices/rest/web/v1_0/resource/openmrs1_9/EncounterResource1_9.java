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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.Encounter;
import org.openmrs.EncounterProvider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * {@link Resource} for {@link EncounterResource1_9}, supporting standard CRUD
 * operations
 */
@Resource(name = RestConstants.VERSION_1 + "/encounter", order = 100, supportedClass = Encounter.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class EncounterResource1_9
		extends
		org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.EncounterResource1_8 {

	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = super
					.getRepresentationDescription(rep);
			description.addProperty("visit", Representation.REF);
			description.removeProperty("provider");
			description.addProperty("encounterProviders", Representation.REF);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = super
					.getRepresentationDescription(rep);
			description.addProperty("visit", Representation.DEFAULT);
			description.removeProperty("provider");
			description.addProperty("encounterProviders",
					Representation.DEFAULT);
			return description;
		}
		return null;
	}

	@Override
	public DelegatingResourceDescription getUpdatableProperties()
			throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super
				.getUpdatableProperties();
		description.addProperty("encounterProviders");
		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super
				.getCreatableProperties();
		description.addProperty("visit");
		description.addProperty("encounterProviders");
		return description;
	}

	@Override
	public Encounter save(Encounter delegate) {
		// This is a hack to save encounterProviders correctly. Without this
		// they are created without encounter_id in
		// the database.
		for (EncounterProvider ep : delegate.getEncounterProviders()) {
			ep.setEncounter(delegate);
		}
		Context.getEncounterService().saveEncounter(delegate);
		return delegate;
	}

	@PropertyGetter("encounterProviders")
	public static Set<EncounterProvider> getActiveEncounterProviders(
			Encounter instance) {
		return new LinkedHashSet<EncounterProvider>(
				instance.getActiveEncounterProviders());
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_9.RESOURCE_VERSION;
	}

}

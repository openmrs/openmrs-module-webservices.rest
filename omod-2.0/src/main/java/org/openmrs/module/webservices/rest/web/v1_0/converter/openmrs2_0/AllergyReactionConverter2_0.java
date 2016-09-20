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
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs2_0;

import org.openmrs.AllergyReaction;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.ConceptResource1_11;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0.PatientAllergyResource2_0;
import java.util.LinkedHashMap;

/**
 * An implementation of Converter to be able to create a representation from a AllergyReaction when
 * AllergyReaction is used in another resource.
 */
@Handler(supports = AllergyReaction.class, order = 0)
public class AllergyReactionConverter2_0 extends BaseDelegatingConverter<AllergyReaction> {
	
	/**
	 * Gets the {@link DelegatingResourceDescription} for the given representation for this
	 * resource, if it exists
	 * 
	 * @param rep
	 * @return
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		if (rep instanceof DefaultRepresentation) {
			description.addProperty("reaction", Representation.REF);
			description.addProperty("reactionNonCoded");
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("reaction", Representation.DEFAULT);
			description.addProperty("reactionNonCoded");
		}
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getByUniqueId(java.lang.String)
	 */
	@Override
	public AllergyReaction getByUniqueId(String string) {
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#newInstance(java.lang.String)
	 */
	@Override
	public AllergyReaction newInstance(String type) {
		return new AllergyReaction();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#asRepresentation(T,
	 *      org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public SimpleObject asRepresentation(AllergyReaction instance, Representation rep) throws ConversionException {
		SimpleObject allergenReactionObject = new SimpleObject();
		Concept reaction = instance.getReaction();
		ConceptResource1_11 conceptResource = (ConceptResource1_11) Context.getService(RestService.class)
		        .getResourceBySupportedClass(Concept.class);
		allergenReactionObject.add("reaction", conceptResource.asRepresentation(reaction, rep));
		allergenReactionObject.add("reactionNonCoded", instance.getReactionNonCoded());
		return allergenReactionObject;
	}
}

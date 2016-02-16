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

import org.openmrs.Allergen;
import org.openmrs.AllergenType;
import org.openmrs.Concept;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.ConceptResource1_11;
import org.apache.commons.lang.StringUtils;
import java.util.LinkedHashMap;

/**
 * An implementation of Converter to be able to create a representation from a Allergen
 * when Allergen is used in another resource. 
 */
@Handler(supports = Allergen.class, order = 0)
public class AllergenConverter2_0 extends BaseDelegatingConverter<Allergen> {
	
	/**
	 * Gets the {@link DelegatingResourceDescription} for the given representation for this
	 * resource, if it exists
	 * 
	 * @param rep
	 * @return
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep)
	{
		RefRepresentation refRep = new RefRepresentation();
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("allergenType");
		description.addProperty("codedAllergen");
		description.addProperty("nonCodedAllergen");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getByUniqueId(java.lang.String)
	 */
	@Override
	public Allergen getByUniqueId(String string) {
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#newInstance(java.lang.String)
	 */
	@Override
	public Allergen newInstance(String type) {
		return new Allergen();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#asRepresentation(T, org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public SimpleObject asRepresentation(Allergen instance, Representation rep) throws ConversionException {
		SimpleObject allergenObject = new SimpleObject();
		allergenObject.add("allergenType", instance.getAllergenType());
		if (instance.isCoded()) {
			ConceptResource1_11 conceptResource = (ConceptResource1_11) Context.getService(RestService.class).getResourceBySupportedClass(Concept.class);
			allergenObject.add("codedAllergen", conceptResource.asRepresentation(instance.getCodedAllergen(), rep));
		}
		allergenObject.add("nonCodedAllergen", instance.getNonCodedAllergen());
		return allergenObject;
	}	
}

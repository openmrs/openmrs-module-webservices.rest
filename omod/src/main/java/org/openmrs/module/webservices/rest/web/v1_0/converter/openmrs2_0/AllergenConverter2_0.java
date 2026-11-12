/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs2_0;

import org.openmrs.Allergen;
import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * An implementation of Converter to be able to create a representation from a Allergen when
 * Allergen is used in another resource.
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
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		if (rep instanceof DefaultRepresentation) {
			description.addProperty("allergenType", Representation.REF);
			description.addProperty("codedAllergen", Representation.REF);
			description.addProperty("nonCodedAllergen");
			return description;
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("allergenType", Representation.DEFAULT);
			description.addProperty("codedAllergen", Representation.DEFAULT);
			description.addProperty("nonCodedAllergen");
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#getByUniqueId(String)
	 */
	@Override
	public Allergen getByUniqueId(String string) {
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.Converter#newInstance(String)
	 */
	@Override
	public Allergen newInstance(String type) {
		return new Allergen();
	}
	

}

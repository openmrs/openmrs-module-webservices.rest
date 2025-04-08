/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.helper;

import io.swagger.models.ModelImpl;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.ObjectProperty;
import io.swagger.models.properties.StringProperty;
import org.openmrs.module.webservices.docs.swagger.core.property.EnumProperty;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

public class LayoutTemplateRepresentation {
	
	public static DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("displayName", rep);
		description.addProperty("codeName", rep);
		description.addProperty("country", rep);
		description.addProperty("lines", rep);
		description.addProperty("lineByLineFormat", rep);
		description.addProperty("nameMappings", rep);
		description.addProperty("sizeMappings", rep);
		description.addProperty("elementDefaults", rep);
		description.addProperty("elementRegex", rep);
		description.addProperty("elementRegexFormats", rep);
		description.addProperty("requiredElements", rep);
		return description;
	}
	
	public static ModelImpl getGETModel(Class<? extends Enum<?>> clsTokenEnum) {
		return new ModelImpl()
				.property("displayName", new StringProperty())
				.property("codeName", new StringProperty())
				.property("country", new StringProperty())
				.property("lines", new ArrayProperty(
						new ArrayProperty(
								new ObjectProperty()
										.property("isToken", new EnumProperty(clsTokenEnum))
										.property("displayText", new StringProperty())
										.property("codeName", new StringProperty())
										.property("displaySize", new StringProperty())
						)))
				.property("lineByLineFormat", new ArrayProperty(new StringProperty()))
				.property("nameMappings", new MapProperty().additionalProperties(new StringProperty()))
				.property("sizeMappings", new MapProperty().additionalProperties(new StringProperty()))
				.property("elementDefaults", new MapProperty().additionalProperties(new StringProperty()))
				.property("elementRegex", new MapProperty().additionalProperties(new StringProperty()))
				.property("elementRegexFormats", new MapProperty().additionalProperties(new StringProperty()))
				.property("requiredElements", new ArrayProperty(new StringProperty()));
	}
}

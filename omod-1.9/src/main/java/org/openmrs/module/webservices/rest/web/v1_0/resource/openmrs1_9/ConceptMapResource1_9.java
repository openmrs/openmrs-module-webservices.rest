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

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptMapResource1_8;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.ConceptResource1_8;

import java.util.Arrays;

/**
 * {@link Resource} for {@link ConceptMap}, supporting standard CRUD operations
 */
@SubResource(path = "mapping", parent = ConceptResource1_8.class, supportedClass = ConceptMap.class, supportedOpenmrsVersions = {
        "1.9.* - 9.*" })
public class ConceptMapResource1_9 extends ConceptMapResource1_8 {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("conceptReferenceTerm", Representation.REF);
			description.addProperty("conceptMapType", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("uuid");
			description.addProperty("conceptReferenceTerm", Representation.DEFAULT);
			description.addProperty("conceptMapType", Representation.DEFAULT);
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema != null) {
            if (rep instanceof DefaultRepresentation) {
				schema
						.addProperty("conceptReferenceTerm", new Schema<ConceptReferenceTerm>().$ref("#/components/schemas/ConceptreferencetermGetRef"))
						.addProperty("conceptMapType", new Schema<ConceptMapType>().$ref("#/components/schemas/ConceptmaptypeGetRef"));
			} else if (rep instanceof FullRepresentation) {
				schema
						.addProperty("conceptReferenceTerm", new Schema<ConceptReferenceTerm>().$ref("#/components/schemas/ConceptreferencetermGet"))
						.addProperty("conceptMapType", new Schema<ConceptMapType>().$ref("#/components/schemas/ConceptmaptypeGet"));
			}
			schema.getProperties().remove("source"); //FIXME check
			schema.getProperties().remove("sourceCode");
			schema.getProperties().remove("comment");
		}
		return schema;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Schema<?> getCREATESchema(Representation representation) {
		return new ObjectSchema()
				.addProperty("conceptReferenceTerm", new Schema<ConceptReferenceTerm>().$ref("#/components/schemas/ConceptreferencetermCreate"))
				.addProperty("conceptMapType", new Schema<ConceptMapType>().$ref("#/components/schemas/ConceptmaptypeCreate"))
				.required(Arrays.asList("conceptReferenceTerm", "conceptMapType"));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("conceptReferenceTerm");
		description.addRequiredProperty("conceptMapType");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * Gets the display string for a concept map.
	 * 
	 * @param conceptMap the concept map object.
	 * @return the display string.
	 */
	@Override
	@PropertyGetter("display")
	public String getDisplayString(ConceptMap conceptMap) {
		if (conceptMap.getConceptReferenceTerm() == null || conceptMap.getConceptReferenceTerm().getConceptSource() == null) {
			return "";
		}
		
		String display = conceptMap.getConceptReferenceTerm().getConceptSource().getName() + ": "
		        + conceptMap.getConceptReferenceTerm().getCode();
		if (!StringUtils.isBlank(conceptMap.getConceptReferenceTerm().getName())) {
			display += " (" + conceptMap.getConceptReferenceTerm().getName() + ")";
		}
		return display;
	}
}

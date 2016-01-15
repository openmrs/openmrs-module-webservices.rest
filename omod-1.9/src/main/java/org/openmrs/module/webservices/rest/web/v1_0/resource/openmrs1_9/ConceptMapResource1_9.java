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

import org.apache.commons.lang.StringUtils;
import org.openmrs.ConceptMap;
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

/**
 * {@link Resource} for {@link ConceptMap}, supporting standard CRUD operations
 */
@SubResource(path = "mapping", parent = ConceptResource1_8.class, supportedClass = ConceptMap.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*"})
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

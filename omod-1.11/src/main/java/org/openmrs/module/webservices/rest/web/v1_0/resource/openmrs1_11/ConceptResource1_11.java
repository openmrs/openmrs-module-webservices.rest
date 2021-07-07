/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.mapping.Collection;
import org.openmrs.Concept;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.ConceptResource1_9;

@Resource(name = RestConstants.VERSION_1 + "/concept", order = 2, supportedClass = Concept.class, supportedOpenmrsVersions = {"1.8.*",
"1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*", "2.2.*","2.3.*","2.4.*"})
public class ConceptResource1_11 extends ConceptResource1_9 {
	
	/**
	 * @see DelegatingCrudResource#fullRepresentationDescription(Concept)
	 */
	@Override
	protected DelegatingResourceDescription fullRepresentationDescription(Concept delegate) {
		DelegatingResourceDescription description = super.fullRepresentationDescription(delegate);
		if (delegate.isNumeric()) {
			description.removeProperty("precise");
			description.addProperty("allowDecimal");
			description.addProperty("displayPrecision");
		}
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants1_11.RESOURCE_VERSION;
	}
	@Override
	protected AlreadyPaged<Concept> doGetAll(RequestContext context){
		List<Locale> locales = new ArrayList<Locale>();
		locales.add(Context.getLocale());
		
		List<Concept> result = Context.getConceptService().getAllConcepts();
		List<Concept> concepts = new ArrayList<Concept>();
		   for (Concept concept : result) {
//			    concepts.add((Collection) concept.getConceptClass());
			    StringBuilder sb = new StringBuilder("result");
			    for(Concept concept1:result) {
			    	   sb.append("concept").append(result);
			    }
		}
		   boolean hasMoreResults = false;
			if (concepts.size() == context.getLimit()) {
				Integer count = Context.getConceptService().getCountOfConcepts(null, null, context.getIncludeAll(), null, null,
				    null, null, null);

				Integer fetchedCount = context.getStartIndex() + concepts.size();
				hasMoreResults = (fetchedCount < count);
			}
			return new AlreadyPaged<Concept>(context, concepts, hasMoreResults);
		}
}

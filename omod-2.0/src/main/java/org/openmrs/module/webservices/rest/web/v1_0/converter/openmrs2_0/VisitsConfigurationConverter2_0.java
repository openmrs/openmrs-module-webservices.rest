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

import org.openmrs.VisitType;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9.VisitTypeResource1_9;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.VisitsConfiguration;

import java.util.ArrayList;
import java.util.List;

@Handler(supports = VisitsConfiguration.class, order = 0)
public class VisitsConfigurationConverter2_0 extends BaseDelegatingConverter<VisitsConfiguration> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("visitEncounterHandler");
		description.addProperty("enableVisits");
		description.addProperty("startAutoCloseVisitsTask");
		if (rep instanceof FullRepresentation) {
			description.addProperty("visitTypesToAutoClose");
			return description;
		}
		return null;
	}

	@Override
	public VisitsConfiguration newInstance(String type) {
		return new VisitsConfiguration();
	}

	@Override
	public VisitsConfiguration getByUniqueId(String string) {
		return null;
	}

	@Override
	public SimpleObject asRepresentation(VisitsConfiguration delegate, Representation rep) throws ConversionException {
		SimpleObject configuration = new SimpleObject();
		configuration.add("visitEncounterHandler", delegate.getVisitEncounterHandler());
		configuration.add("enableVisits", delegate.getEnableVisits());
		configuration.add("startAutoCloseVisitsTask", delegate.getStartAutoCloseVisitsTask());

		if (rep instanceof FullRepresentation) {
			VisitTypeResource1_9 visitTypeResource = (VisitTypeResource1_9) Context.getService(RestService.class)
					.getResourceBySupportedClass(VisitType.class);
			List<SimpleObject> visitTypesToAutoClose = new ArrayList<>();
			for (VisitType visitType : delegate.getVisitTypesToAutoClose()) {
				try {
					visitTypesToAutoClose.add(visitTypeResource.asDefaultRep(visitType));
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			configuration.add("visitTypesToAutoClose", visitTypesToAutoClose);
		}
		return configuration;
	}
}

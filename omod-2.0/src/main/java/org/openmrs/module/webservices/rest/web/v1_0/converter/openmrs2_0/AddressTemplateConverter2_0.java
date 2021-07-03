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

import org.openmrs.annotation.Handler;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.AddressTemplateXml;

@Handler(supports = AddressTemplateXml.class, order = 0)
public class AddressTemplateConverter2_0 extends BaseDelegatingConverter<AddressTemplateXml> {

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("addressTemplateXml");
		return description;
	}

	@Override
	public AddressTemplateXml newInstance(String type) {
		return new AddressTemplateXml();
	}

	@Override
	public AddressTemplateXml getByUniqueId(String string) {
		return null;
	}

	@Override
	public SimpleObject asRepresentation(AddressTemplateXml delegate, Representation rep) throws ConversionException {
		SimpleObject addressTemplateObject = new SimpleObject();
		addressTemplateObject.add("addressTemplateXml", delegate.getAddressTemplateXml());
		return addressTemplateObject;
	}
}

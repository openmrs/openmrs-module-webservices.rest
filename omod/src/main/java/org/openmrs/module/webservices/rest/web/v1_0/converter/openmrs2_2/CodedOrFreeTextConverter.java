/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.converter.openmrs2_2;

import org.openmrs.CodedOrFreeText;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingConverter;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

@Handler(supports = CodedOrFreeText.class, order = 0)
public class CodedOrFreeTextConverter extends BaseDelegatingConverter<CodedOrFreeText> {
	
	@Override
	public SimpleObject asRepresentation(CodedOrFreeText instance, Representation rep) throws ConversionException {
		SimpleObject codedOrFreeText = new SimpleObject();
		if (rep instanceof CustomRepresentation) {
			CustomRepresentation customRep = (CustomRepresentation) rep;
			DelegatingResourceDescription drd = ConversionUtil.getCustomRepresentationDescription(customRep);
			for (String propertyName : drd.getProperties().keySet()) {
				DelegatingResourceDescription.Property property = drd.getProperties().get(propertyName);
				Object o = ConversionUtil.getPropertyWithRepresentation(instance, propertyName, property.getRep());
				if (o != null) {
					codedOrFreeText.add(propertyName, o);
				}
			}
		}
		else {
			// through ConversionUtil rather than straight to the resource, so these two get the privilege
			// check and the authorization-rule check every other nested object gets. Calling
			// asRepresentation directly meant a refused concept was masked under
			// v=custom:(condition:(coded)), which goes via getPropertyWithRepresentation, and served in
			// full under the default and full representations, which come through here
			addUnlessPrivilegeDenied(codedOrFreeText, "specificName", instance.getSpecificName(), rep);
			addUnlessPrivilegeDenied(codedOrFreeText, "coded", instance.getCoded(), rep);

			if (instance.getNonCoded() != null) {
				codedOrFreeText.add("nonCoded", instance.getNonCoded());
			}
		}
		
		return codedOrFreeText;
	}

	/**
	 * Adds {@code value}'s representation under {@code propertyName}, leaving the key out when the
	 * current user lacks the privilege its resource requires - the same thing
	 * {@code convertDelegateToRepresentation} does for a property of a delegating resource, so a
	 * privilege denial reads the same here as it does everywhere else. A null value adds nothing.
	 */
	private void addUnlessPrivilegeDenied(SimpleObject codedOrFreeText, String propertyName, Object value,
	        Representation rep) throws ConversionException {
		if (value == null) {
			return;
		}
		Object converted = ConversionUtil.convertToRepresentation(value, rep);
		if (converted != ConversionUtil.PRIVILEGE_DENIED) {
			codedOrFreeText.add(propertyName, converted);
		}
	}

	@Override
	public CodedOrFreeText getByUniqueId(String uuid) {
		return null;
	}
	
	@Override
	public CodedOrFreeText newInstance(String type) {
		return new CodedOrFreeText();
	}
	
	@Override
	public void setProperty(Object instance, String propertyName, Object value) throws ConversionException {
		if (propertyName.equals("specificName")) {
			((CodedOrFreeText) instance).setSpecificName(Context.getConceptService().getConceptNameByUuid((String) value));
		}
		else if (propertyName.equals("coded")) {
			((CodedOrFreeText) instance).setCoded(Context.getConceptService().getConceptByUuid((String) value));
		}
		else if (propertyName.equals("nonCoded")) {
			((CodedOrFreeText) instance).setNonCoded((String) value);
		}
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		if (rep instanceof DefaultRepresentation) {
			description.addProperty("specificName", Representation.REF);
			description.addProperty("coded", Representation.REF);
			description.addProperty("nonCoded");
		} else if (rep instanceof FullRepresentation) {
			description.addProperty("specificName", Representation.DEFAULT);
			description.addProperty("coded", Representation.DEFAULT);
			description.addProperty("nonCoded");
		}
		return description;
	}
}

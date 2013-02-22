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
package org.openmrs.module.webservices.rest.web.resource.impl;

import org.openmrs.OpenmrsData;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.response.ConversionException;

/**
 * Subclass of {@link DelegatingCrudResource} with helper methods specific to {@link OpenmrsData}
 * @param <T>
 */
public abstract class DataDelegatingCrudResource<T extends OpenmrsData> extends DelegatingCrudResource<T> {
	
	@RepHandler(RefRepresentation.class)
	public SimpleObject asRef(T delegate) throws ConversionException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display", findMethod("getDisplayString"));
		if (delegate.isVoided())
			description.addProperty("voided");
		description.addSelfLink();
		return convertDelegateToRepresentation(delegate, description);
	}
	
	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(T delegate) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("uuid", delegate.getUuid());
		ret.put("display", delegate.toString());
		ret.put("voided", delegate.isVoided());
		ret.put("links", "[ All Data resources need to define their representations ]");
		return ret;
	}
	
	public SimpleObject getAuditInfo(T delegate) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(delegate, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(delegate.getDateCreated(), Representation.DEFAULT));
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(delegate, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(delegate.getDateChanged(), Representation.DEFAULT));
		if (delegate.isVoided()) {
			ret.put("voidedBy", ConversionUtil.getPropertyWithRepresentation(delegate, "voidedBy", Representation.REF));
			ret.put("dateVoided", ConversionUtil.convertToRepresentation(delegate.getDateVoided(), Representation.DEFAULT));
			ret.put("voidReason", ConversionUtil.convertToRepresentation(delegate.getVoidReason(), Representation.DEFAULT));
		}
		return ret;
	}
	
}

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
package org.openmrs.module.webservices.rest.web;

import java.beans.PropertyEditorSupport;

import org.openmrs.OpenmrsObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.api.WSRestService;
import org.springframework.util.StringUtils;

/**
 * Allows for serializing/deserializing any OpenmrsObject by its uuid.
 * 
 * @see OpenmrsObject
 */
public class OpenmrsObjectByUuidEditor extends PropertyEditorSupport {

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	public void setAsText(String uuid) throws IllegalArgumentException {

		if (StringUtils.hasText(uuid)) {
			WSRestService service = Context.getService(WSRestService.class);

			try {
				setValue(service.getOpenmrsObjectByUuid(((OpenmrsObject)getValue()).getClass(), uuid));
			} catch (Exception ex) {
				throw new IllegalArgumentException("OpenmrsObject not found: "
						+ ex.getMessage(), ex);
			}
		} else {
			setValue(null);
		}
	}

	/**
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	public String getAsText() {
		OpenmrsObject oo = (OpenmrsObject) getValue();
		if (oo == null) {
			return "";
		} else {
			return oo.getUuid();
		}
	}

}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.security;

import org.openmrs.BaseOpenmrsObject;

/**
 * A domain object with no REST resource registered for it, so {@code ConversionUtil.getConverter}
 * finds nothing for it and {@code convertToRepresentation} reaches its no-converter shortcut. That
 * shortcut returns the object whole for the serializer to write, which is what makes the position of
 * the masking check above it worth a test of its own.
 * <p>
 * Non-final with an accessible no-arg constructor, because {@code Authorize.standInFor} masks by
 * subclassing the entity class.
 */
public class UnregisteredTestObject extends BaseOpenmrsObject {

	private Integer id;

	private String secret;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	/** What a reader must not be handed when a rule refuses this object. */
	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}

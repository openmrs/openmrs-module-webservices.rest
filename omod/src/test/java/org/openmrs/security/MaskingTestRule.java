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

import java.io.Serializable;

import org.openmrs.OpenmrsObject;
import org.springframework.security.core.Authentication;

/**
 * Refuses one test-selected record of a single type, so a test can see what a representation does
 * with an object a rule hides. Each subclass covers one type, because
 * {@link DomainObjectAuthorizationRule#getTargetType()} names one and
 * {@code OpenmrsPermissionEvaluator} matches rules by it.
 * <p>
 * Subclasses live in {@code org.openmrs.security} because that is a package core's component scan
 * reaches, which is how a rule becomes visible to {@code OpenmrsPermissionEvaluator}. Each refuses
 * nothing until a test sets its {@code deniedId}, so having them on the test classpath does not
 * change behaviour for tests that do not use them.
 *
 * @param <T> the type this rule judges
 */
public abstract class MaskingTestRule<T extends OpenmrsObject> implements DomainObjectAuthorizationRule {

	/**
	 * The id this rule refuses, or null to refuse nothing. Held by the subclass rather than here so a
	 * test can set it without reaching for the registered component.
	 */
	protected abstract Integer deniedId();

	/** The id of one object of this rule's target type. */
	protected abstract Integer idOf(T object);

	@Override
	@SuppressWarnings("unchecked")
	public boolean isAuthorized(Authentication authentication, Object targetDomainObject, Object permission) {
		Integer denied = deniedId();
		if (denied == null || !getTargetType().isInstance(targetDomainObject)) {
			return true;
		}
		return !denied.equals(idOf((T) targetDomainObject));
	}

	@Override
	public boolean isAuthorized(Authentication authentication, Serializable targetId, Class<?> targetType,
	        Object permission) {
		Integer denied = deniedId();
		return denied == null || !denied.equals(targetId);
	}
}

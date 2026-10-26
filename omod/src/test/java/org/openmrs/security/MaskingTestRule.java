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
 * Subclasses live in {@code org.openmrs.security} because {@code OpenmrsPermissionEvaluator}'s
 * registration hook is package-private; a test reaches it through {@link AuthorizationRules}.
 * <p>
 * None of them is a bean, and a test registers the ones it needs for its own duration.
 * Contributing them to the context would change which path a read takes in every other test in this
 * module even while they refuse nothing: the evaluator would report that rules exist, and
 * {@code Authorize} then copies a collection instead of returning a view of it, initializing a lazy
 * association on the way. Registered per test, everything else runs the no-rule path a server
 * without a rule module takes.
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

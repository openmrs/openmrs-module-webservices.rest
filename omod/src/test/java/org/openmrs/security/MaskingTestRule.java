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

import org.openmrs.Person;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Refuses one test-selected person, so a test can see what a representation does with an object a
 * rule hides. Declared in {@code org.openmrs.security} because that is a package core's component
 * scan reaches, which is how a rule becomes visible to {@code OpenmrsPermissionEvaluator}.
 * <p>
 * Refuses nothing until a test sets {@link #deniedPersonId}, so having it on the test classpath does
 * not change behaviour for tests that do not use it.
 */
@Component
public class MaskingTestRule implements DomainObjectAuthorizationRule {
	
	public static volatile Integer deniedPersonId;
	
	@Override
	public Class<?> getTargetType() {
		return Person.class;
	}
	
	@Override
	public boolean isAuthorized(Authentication authentication, Object targetDomainObject, Object permission) {
		if (deniedPersonId == null || !(targetDomainObject instanceof Person)) {
			return true;
		}
		return !deniedPersonId.equals(((Person) targetDomainObject).getPersonId());
	}
	
	@Override
	public boolean isAuthorized(Authentication authentication, Serializable targetId, Class<?> targetType,
	        Object permission) {
		if (deniedPersonId == null) {
			return true;
		}
		return targetId instanceof Integer && !deniedPersonId.equals(targetId);
	}
}

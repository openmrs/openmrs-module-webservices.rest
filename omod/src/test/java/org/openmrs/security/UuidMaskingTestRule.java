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
import org.springframework.stereotype.Component;

/**
 * Refuses one test-selected record of any type, named by its uuid.
 * <p>
 * Each {@link MaskingTestRule} subclass next to this one covers a single type and names its record
 * by the primary key, which is how a rule written for a deployment reads. This covers the long tail:
 * the records a display string reads off its delegate run to visit types, locations, encounter
 * types, programs, identifiers, identifier types, names and attribute types, and one class per type
 * would be a file of boilerplate each to serve one test. A uuid names any {@link OpenmrsObject}
 * without the rule having to know which getter holds its id.
 * <p>
 * Which overload refuses is a test's choice, because it decides what the layer under test is even
 * handed. {@link #deniedUuid} judges objects only: a service method's
 * {@code hasPermission(#uuid, 'Concept', ...)} guard asks the id-and-type overload, and refusing
 * there makes the service throw before the presentation layer is handed the record at all - which is
 * a real way to deny a read, but not the one most of these tests are about. The
 * {@link MaskingTestRule} subclasses judge objects only too, without saying so, since an
 * {@code Integer} denied id never equals the uuid such a guard passes.
 * <p>
 * {@link #deniedUuidAtServiceGuards} is for the tests that <em>are</em> about it: a rule written for
 * a deployment answers both overloads, so the guard denies and a resource resolving that record
 * through a service meets an exception rather than a mask. It refuses the object as well, since a
 * rule that denies a lookup by id would hardly authorize the same record by reference.
 * <p>
 * Refuses nothing until a test sets one of them, so being on the test classpath changes nothing for
 * tests that do not use it.
 */
@Component
public class UuidMaskingTestRule implements DomainObjectAuthorizationRule {

	/** The uuid this rule refuses when asked about an object, or null to refuse nothing. */
	public static volatile String deniedUuid;

	/** The uuid this rule refuses however it is asked, a service guard's id included. */
	public static volatile String deniedUuidAtServiceGuards;

	@Override
	public Class<?> getTargetType() {
		return OpenmrsObject.class;
	}

	@Override
	public boolean isAuthorized(Authentication authentication, Object targetDomainObject, Object permission) {
		if (!(targetDomainObject instanceof OpenmrsObject)) {
			return true;
		}
		String uuid = ((OpenmrsObject) targetDomainObject).getUuid();
		return !refuses(deniedUuid, uuid) && !refuses(deniedUuidAtServiceGuards, uuid);
	}

	@Override
	public boolean isAuthorized(Authentication authentication, Serializable targetId, Class<?> targetType,
	        Object permission) {
		return !refuses(deniedUuidAtServiceGuards, targetId);
	}

	private static boolean refuses(String denied, Object candidate) {
		return denied != null && denied.equals(candidate);
	}
}

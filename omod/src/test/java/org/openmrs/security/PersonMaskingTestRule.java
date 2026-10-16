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

import org.openmrs.Person;
import org.springframework.stereotype.Component;

/**
 * Refuses one person, and so also the patient, user or provider's person behind it: a rule's target
 * type is matched by assignability, so {@code Person} covers every subtype.
 */
@Component
public class PersonMaskingTestRule extends MaskingTestRule<Person> {

	public static volatile Integer deniedId;

	@Override
	public Class<?> getTargetType() {
		return Person.class;
	}

	@Override
	protected Integer deniedId() {
		return deniedId;
	}

	@Override
	protected Integer idOf(Person person) {
		return person.getPersonId();
	}
}

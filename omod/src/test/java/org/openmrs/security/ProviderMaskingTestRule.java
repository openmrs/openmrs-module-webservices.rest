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

import org.openmrs.Provider;

/**
 * Refuses one provider. Distinct from {@link PersonMaskingTestRule}: a display reading a provider
 * has to ask about both, since a rule may cover the provider and not the person behind it.
 */
public class ProviderMaskingTestRule extends MaskingTestRule<Provider> {

	public static volatile Integer deniedId;

	@Override
	public Class<?> getTargetType() {
		return Provider.class;
	}

	@Override
	protected Integer deniedId() {
		return deniedId;
	}

	@Override
	protected Integer idOf(Provider provider) {
		return provider.getProviderId();
	}
}

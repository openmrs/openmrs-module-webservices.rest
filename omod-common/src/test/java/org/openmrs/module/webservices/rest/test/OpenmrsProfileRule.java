/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.test;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openmrs.module.ModuleUtil;
import org.openmrs.util.OpenmrsConstants;

/**
 * Allows to execute tests only on the specific version of OpenMRS.
 * Use with {@code @RegisterExtension} in JUnit 5 tests.
 */
public class OpenmrsProfileRule implements ExecutionCondition {
	
	private final String[] openmrsVersions;
	
	/**
	 * Allows to specify versions of OpenMRS on which tests should be executed.
	 * 
	 * @param openmrsVersion
	 * @param openmrsVersions
	 */
	public OpenmrsProfileRule(String openmrsVersion, String... openmrsVersions) {
		int length = openmrsVersions.length;
		this.openmrsVersions = Arrays.copyOf(openmrsVersions, length + 1);
		this.openmrsVersions[length] = openmrsVersion;
	}
	
	/**
	 * Allows to specify a version of OpenMRS on which tests should be executed.
	 * 
	 * @param openmrsVersion
	 */
	public OpenmrsProfileRule(String openmrsVersion) {
		this.openmrsVersions = new String[] { openmrsVersion };
	}
	
	@Override
	public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
		for (String openmrsVersion : openmrsVersions) {
			if (ModuleUtil.matchRequiredVersions(OpenmrsConstants.OPENMRS_VERSION_SHORT, openmrsVersion)) {
				return ConditionEvaluationResult.enabled("OpenMRS version matches " + openmrsVersion);
			}
		}
		return ConditionEvaluationResult.disabled("Test skipped (run only on OpenMRS "
		        + StringUtils.join(openmrsVersions, ",") + ")");
	}
	
}

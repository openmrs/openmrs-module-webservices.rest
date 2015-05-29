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
package org.openmrs.module.webservices.rest.test;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.openmrs.module.ModuleUtil;
import org.openmrs.util.OpenmrsConstants;

/**
 * Allows to execute tests only on the specific version of OpenMRS.
 */
public class OpenmrsProfileRule implements TestRule {
	
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
	 * @param openmrsVersions
	 */
	public OpenmrsProfileRule(String openmrsVersion) {
		this.openmrsVersions = new String[] { openmrsVersion };
	}
	
	/**
	 * @see org.junit.rules.TestRule#apply(org.junit.runners.model.Statement,
	 *      org.junit.runner.Description)
	 */
	@Override
	public Statement apply(Statement base, Description description) {
		return new OpenmrsProfileStatement(base, description);
	}
	
	private class OpenmrsProfileStatement extends Statement {
		
		private final Statement base;
		
		private final Description description;
		
		public OpenmrsProfileStatement(Statement base, Description description) {
			this.base = base;
			this.description = description;
		}
		
		/**
		 * @see org.junit.runners.model.Statement#evaluate()
		 */
		@Override
		public void evaluate() throws Throwable {
			for (String openmrsVersion : openmrsVersions) {
				try {
					ModuleUtil.checkRequiredVersion(OpenmrsConstants.OPENMRS_VERSION_SHORT, openmrsVersion);
					base.evaluate();
					return;
				}
				catch (Exception e) {}
			}
			System.out.println("Ignored " + description.getMethodName() + " (run only on OpenMRS "
			        + StringUtils.join(openmrsVersions, ",") + ")");
		}
		
	}
	
}

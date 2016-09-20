/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.docs;

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
import java.util.ArrayList;
import java.util.List;

import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;

public class SearchQueryDoc {
	
	private String description;
	
	private List<String> requiredParameters;
	
	private List<String> optionalParameters;
	
	public SearchQueryDoc(SearchQuery searchQuery) {
		this.description = searchQuery.getDescription();
		this.requiredParameters = new ArrayList<String>(searchQuery.getRequiredParameters());
		this.optionalParameters = new ArrayList<String>(searchQuery.getOptionalParameters());
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the requiredParameters
	 */
	public List<String> getRequiredParameters() {
		return requiredParameters;
	}
	
	/**
	 * @param requiredParameters the requiredParameters to set
	 */
	public void setRequiredParameters(List<String> requiredParameters) {
		this.requiredParameters = requiredParameters;
	}
	
	/**
	 * @return the optionalParameters
	 */
	public List<String> getOptionalParameters() {
		return optionalParameters;
	}
	
	/**
	 * @param optionalParameters the optionalParameters to set
	 */
	public void setOptionalParameters(List<String> optionalParameters) {
		this.optionalParameters = optionalParameters;
	}
	
}

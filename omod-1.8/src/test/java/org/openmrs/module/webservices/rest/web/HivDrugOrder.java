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
package org.openmrs.module.webservices.rest.web;

import org.openmrs.DrugOrder;

/**
 * This is a contrived example for testing purposes
 */
public class HivDrugOrder extends DrugOrder {
	
	private static final long serialVersionUID = 1L;
	
	// just a plain DrugOrder works fine for testing.
	// We use @PropertySetter and @PropertyGetter in the subclass handler to expose a virtual "standardRegimenCode" property
	
}

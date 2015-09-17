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

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.stereotype.Component;

/**
 * Setups xstream marshaller to support annotated classes.
 */
@Component
public class RestInit {
	
	@Autowired
	@Qualifier("xStreamMarshaller")
	XStreamMarshaller marshaller;
	
	@PostConstruct
	public void init() {
		marshaller.setAutodetectAnnotations(true);
	}
}

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
package org.openmrs.module.webservices.rest.web.controller;

import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.WSConstants;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 */
@Controller
@RequestMapping(value = "/rest")
public class ProgramController extends BaseResourceController {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	/**
	 * @param program
	 * @param data
	 * @return
	 * @throws ResponseException
	 */
	@RequestMapping(value = "/program/{programUuid}/enrollment", method = RequestMethod.POST)
	@ResponseBody
	public SimpleObject enrollPatientInProgram(@PathVariable("programUuid") Program program,
	                                           @RequestBody Map<String, Object> data) throws ResponseException {
		
		PatientProgram pp = new PatientProgram();
		// TODO: figure out how to use the prop editors here 
		pp.setPatient(Context.getPatientService().getPatientByUuid(data.get("patient").toString()));
		pp.setDateEnrolled(new Date()); // pull from param
		pp.setProgram(program);
		
		Context.getProgramWorkflowService().savePatientProgram(pp);
		
		// return the newly changed program object
		try {
			// TODO: might not return the actual enrollment with default rep, use full or custom?
			return wsUtil.convert(program, WSConstants.REPRESENTATION_DEFAULT);
		} catch (Exception e) {
			log.error("Unable get program " + program, e);
			throw new ConversionException();
		}
	}
	
}

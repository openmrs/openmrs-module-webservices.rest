/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import com.google.common.net.HttpHeaders;
import org.apache.commons.io.IOUtils;
import org.openmrs.api.DatatypeService;
import org.openmrs.api.db.ClobDatatypeStorage;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping(value = "/rest/" + RestConstants.VERSION_1 + "/clobdata")
public class ClobDatatypeStorageController {
	
	@Autowired
	private DatatypeService datatypeService;
	
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String create(@RequestParam MultipartFile file, HttpServletRequest request, HttpServletResponse response)
	        throws IOException {
		ClobDatatypeStorage clobData = new ClobDatatypeStorage();
		// Read the upload as UTF-8 to pair with the UTF-8 output in retrieve(). The Content-Encoding request
		// header describes compression (e.g. gzip), not a charset, so it must not be used as the read charset.
		clobData.setValue(IOUtils.toString(file.getInputStream(), StandardCharsets.UTF_8));
		clobData = datatypeService.saveClobDatatypeStorage(clobData);
		response.setStatus(HttpServletResponse.SC_CREATED);
		return clobData.getUuid();
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/{uuid}")
	public void retrieve(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response)
	        throws Exception {
		ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(uuid);
		
		if (clobData == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} else {
			// Serve the stored content as plain text and instruct browsers not to MIME-sniff it. Clob content is
			// arbitrary, unsanitized user input, so without these headers a browser can interpret an uploaded HTML
			// payload as HTML and execute embedded scripts, resulting in stored XSS.
			response.setContentType("text/plain;charset=UTF-8");
			response.setHeader(HttpHeaders.X_CONTENT_TYPE_OPTIONS, "nosniff");
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.print(clobData.getValue());
				writer.flush();
			}
			finally {
				if (writer != null) {
					writer.close();
				}
			}
		}
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{uuid}")
	public void delete(@PathVariable("uuid") String uuid, HttpServletRequest request, HttpServletResponse response) {
		ClobDatatypeStorage clobData = datatypeService.getClobDatatypeStorageByUuid(uuid);
		if (clobData != null) {
			datatypeService.deleteClobDatatypeStorage(clobData);
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}
	}
}

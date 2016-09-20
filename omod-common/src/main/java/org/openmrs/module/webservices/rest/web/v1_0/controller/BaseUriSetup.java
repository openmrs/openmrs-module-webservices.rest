package org.openmrs.module.webservices.rest.web.v1_0.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Component;

@Component
public class BaseUriSetup {
	
	public void setup(HttpServletRequest request) {
		if (!RestConstants.URI_PREFIX.startsWith("http://") && !RestConstants.URI_PREFIX.startsWith("https://")) {
			StringBuilder uri = new StringBuilder();
			uri.append(request.getScheme()).append("://").append(request.getServerName());
			if (request.getServerPort() != 80) {
				uri.append(":").append(request.getServerPort());
			}
			if (!StringUtils.isBlank(request.getContextPath())) {
				uri.append(request.getContextPath());
			}
			
			RestConstants.URI_PREFIX = uri.toString() + RestConstants.URI_PREFIX;
		}
	}
}

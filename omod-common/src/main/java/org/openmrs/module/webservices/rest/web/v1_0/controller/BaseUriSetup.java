package org.openmrs.module.webservices.rest.web.v1_0.controller;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.apache.jasper.tagplugins.jstl.core.Url;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.springframework.stereotype.Component;

@Component
public class BaseUriSetup {
	
	public void setup(HttpServletRequest request) {
		if (RestConstants.URI_PREFIX.contains("NEED-TO-CONFIGURE")) {
			int port = request.getServerPort();
			port = (port == 80) ? -1 : port;
			
			try {
				URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(),
				        request.getContextPath());
				RestConstants.URI_PREFIX = RestConstants.URI_PREFIX.replace("NEED-TO-CONFIGURE", url.toString());
			}
			catch (MalformedURLException e) {
				throw new IllegalStateException(e);
			}
		}
	}
}

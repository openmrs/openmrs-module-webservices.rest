package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.APIException;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
//import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import org.mockito.Matchers;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;

import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.api.AdministrationService;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.docs.swagger.SwaggerSpecificationCreator;

public class SwaggerSpecificationControllerTest {
	
	private HttpServletRequest req = mock(HttpServletRequest.class);
	
	private AdministrationService ad = mock(AdministrationService.class);
	private RestService re = mock(RestService.class);
	private SwaggerSpecificationController ssc = new SwaggerSpecificationController();
	
	private Context con = new Context();
	
	@Before
	public void setup() {
		ServiceContext scon = ServiceContext.getInstance();
		con.setServiceContext(scon);
		scon.setAdministrationService(ad);
		scon.setService(RestService.class, re);
	}
	
	@Test
	public void httpTest() throws Exception {
		
		when(req.getScheme()).thenReturn("asdf");
		when(req.getServerPort()).thenReturn(443);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		when(re.getAllSearchHandlers().thenReturn());
		
		doReturn("resourceURL").when(ad).getGlobalProperty(anyString(), anyString());
		
		
		ssc = new SwaggerSpecificationController();
		String res = ssc.getSwaggerSpecification(req);
		System.out.println("hey");
		
	}
	
}

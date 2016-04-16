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

public class SwaggerSpecificationControllerTest {
	
	private HttpServletRequest req = mock(HttpServletRequest.class);
	
	private ServiceContext scontext = mock(ServiceContext.class);
	
	private AdministrationService ad = mock(AdministrationService.class);
	
	private SwaggerSpecificationController ssc = new SwaggerSpecificationController();
	
	private Context con = new Context();
	
	@Test
	public void httpTest() throws Exception {
		con.setServiceContext(scontext);
		when(req.getScheme()).thenReturn("asdf");
		when(req.getServerPort()).thenReturn(443);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		
		when(scontext.getService(AdministrationService.class).thenReturn(ad));
		when(scontext.getAdministrationService().thenReturn(ad));//having issues mocking this particular method
		
		when(ad.getGlobalProperty(anyString(), anyString()).thenReturn("resourceURL"));
		
		ssc.getSwaggerSpecification(req);
		
	}
	
}

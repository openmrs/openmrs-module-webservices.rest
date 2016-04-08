package org.openmrs.module.webservices.rest.web.controller;

import org.openmrs.api.APIException;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
//import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import org.junit.Test;

public class SwaggerSpecificationControllerTest {
	
	private HttpServletRequest req = mock(HttpServletRequest.class);
	
	private SwaggerSpecificationController ssc = new SwaggerSpecificationController();
	
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_0100() throws Exception {
		
		when(req.getScheme()).thenReturn("asdf");
		when(req.getServerPort()).thenReturn(443);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	//quick commit by rev
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_1100() throws Exception {
		
		when(req.getScheme()).thenReturn("http");
		when(req.getServerPort()).thenReturn(81);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_0001() throws Exception {
		
		when(req.getScheme()).thenReturn("asdf");
		when(req.getServerPort()).thenReturn(80);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_0010() throws Exception {
		
		when(req.getScheme()).thenReturn("https");
		when(req.getServerPort()).thenReturn(443);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_0011() throws Exception {
		
		when(req.getScheme()).thenReturn("https");
		when(req.getServerPort()).thenReturn(80);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	@Test(expected = org.openmrs.api.APIException.class)
	public void test_1000() throws Exception {
		
		when(req.getScheme()).thenReturn("https");
		when(req.getServerPort()).thenReturn(443);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getServerPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	
	//
}

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
	
	@Test(expected=org.openmrs.api.APIException.class)
	public void test_1100() throws Exception {
		
		when(req.getScheme()).thenReturn("http");
		when(req.getServerPort()).thenReturn(81);
		when(req.getServerName()).thenReturn("name");
		when(req.getContextPath()).thenReturn("path");
		//when(req.getScheme()).thenReturn("asdasda");        
		//when(reg.getPort()).thenReturn(81);
		
		ssc.getSwaggerSpecification(req);
		
	}
	 
	  @Test(expected=org.openmrs.api.APIException.class)
	  public void test_0100() throws Exception{
	      HttpServletRequest req = mock(HttpServletRequest.class);
	      
	      when(req.getScheme()).thenReturn("asdf");
	      when(reg.getPort()).thenReturn(443);
	      
	      getSwaggerSpecification(req);
	  }



	  @Test(expected=org.openmrs.api.APIException.class)
	  public void test_0001() throws Exception{
	      HttpServletRequest req = mock(HttpServletRequest.class);
	      
	      when(req.getScheme()).thenReturn("asdf");
	      when(reg.getPort()).thenReturn(80);
	      
	      getSwaggerSpecification(req);
	  }
	  
	  @Test(expected=org.openmrs.api.APIException.class)
	  public void test_0010() throws Exception{
	      HttpServletRequest req = mock(HttpServletRequest.class);
	      
	      when(req.getScheme()).thenReturn("https");
	      when(reg.getPort()).thenReturn(443);
	      
	      getSwaggerSpecification(req);
	  }
	  
	  @Test(expected=org.openmrs.api.APIException.class)
	  public void test_0011() throws Exception{
	      HttpServletRequest req = mock(HttpServletRequest.class);
	      
	      when(req.getScheme()).thenReturn("https");
	      when(reg.getPort()).thenReturn(80);
	      
	      getSwaggerSpecification(req);
	  }
	  @Test(expected=org.openmrs.api.APIException.class)
	  public void test_1000() throws Exception{
	      HttpServletRequest req = mock(HttpServletRequest.class);
	      
	      when(req.getScheme()).thenReturn("http");
	      when(reg.getPort()).thenReturn(443);
	      
	      getSwaggerSpecification(req);
	  }    
	  
	  
}

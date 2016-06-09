package org.openmrs.module.webservices.rest.web.filter;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert.*;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.api.context.ServiceContext;
import org.openmrs.api.context.UserContext;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.db.ContextDAO;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.RestUtil;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;
import org.mockito.Matchers;

public class AuthorizationFilterTest {
	
	AuthorizationFilter filter = new AuthorizationFilter();
	
	private AdministrationService ad = mock(AdministrationService.class);
	
	private Context con = new Context();
	
	@Before
	public void setup() {
		ServiceContext scon = ServiceContext.getInstance();
		con.setServiceContext(scon);
		scon.setAdministrationService(ad);
	}
	
	@Test
	public void InvalidIPShouldReturn403() throws IOException, ServletException {
		
		ServletRequestWrapper req = mock(ServletRequestWrapper.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		doReturn("localhost").when(ad).getGlobalProperty(anyString(), anyString());
		when(req.getRemoteAddr()).thenReturn("1.1.1.1");
		
		filter.doFilter(req, res, chain);
		
		verify(res).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
		
	}
	
	@Test
	public void ShouldSendErrorIfTimedOut() throws IOException, ServletException {
		ContextDAO dao = mock(ContextDAO.class);
		con.setContextDAO(dao);
		Context.openSession();
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse res = mock(HttpServletResponse.class);
		FilterChain chain = mock(FilterChain.class);
		doReturn("localhost").when(ad).getGlobalProperty(anyString(), anyString());
		when(req.getRemoteAddr()).thenReturn("localhost");
		when(req.isRequestedSessionIdValid()).thenReturn(false);
		when(req.getRequestedSessionId()).thenReturn("x");
		
		filter.doFilter(req, res, chain);
		Context.closeSession();
		verify(res).sendError(eq(HttpServletResponse.SC_FORBIDDEN), anyString());
		
	}
	
}

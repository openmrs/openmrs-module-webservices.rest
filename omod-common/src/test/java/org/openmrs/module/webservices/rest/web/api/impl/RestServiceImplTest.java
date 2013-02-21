package org.openmrs.module.webservices.rest.web.api.impl;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;

public class RestServiceImplTest {
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw exception if no handler with id
	 */
	@Test
	public void getSearchHandler_shouldThrowExceptionIfNoHandlerWithId() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("none");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		service.addSupportedSearchHandler(searchHandler);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("s", "conceptByMapping");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (ResourceDoesNotSupportOperationException e) {}
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler by id if exists
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerByIdIfExists() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("conceptByMapping");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		service.addSupportedSearchHandler(searchHandler);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("s", "conceptByMapping");
		service.getSearchHandler("concept", parameters);
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 1
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase1() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("conceptByMapping");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		when(searchHandler.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName", "code")));
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		when(searchHandler2.getId()).thenReturn("conceptByMapping2");
		when(searchHandler2.getSupportedResource()).thenReturn("concept");
		when(searchHandler2.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		when(searchHandler2.getOptionalParameters()).thenReturn(new HashSet<String>(Arrays.asList("code")));
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		parameters.put("code", "some code");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (ResourceDoesNotSupportOperationException e) {}
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler if case 2
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerIfCase2() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("conceptByMapping");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		when(searchHandler.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		when(searchHandler.getOptionalParameters()).thenReturn(new HashSet<String>(Arrays.asList("code")));
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		when(searchHandler2.getId()).thenReturn("conceptByMapping2");
		when(searchHandler2.getSupportedResource()).thenReturn("concept");
		when(searchHandler2.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		parameters.put("code", "some code");
		SearchHandler searchHandler3 = service.getSearchHandler("concept", parameters);
		
		assertThat(searchHandler3, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 3
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase3() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("conceptByMapping");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		when(searchHandler.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		when(searchHandler.getOptionalParameters()).thenReturn(new HashSet<String>(Arrays.asList("code")));
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		when(searchHandler2.getId()).thenReturn("conceptByMapping2");
		when(searchHandler2.getSupportedResource()).thenReturn("concept");
		when(searchHandler2.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (ResourceDoesNotSupportOperationException e) {}
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if too few parameters
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfTooFewParameters() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		when(searchHandler.getId()).thenReturn("conceptByMapping");
		when(searchHandler.getSupportedResource()).thenReturn("concept");
		when(searchHandler.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		when(searchHandler.getOptionalParameters()).thenReturn(new HashSet<String>(Arrays.asList("code")));
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		when(searchHandler2.getId()).thenReturn("conceptByMapping2");
		when(searchHandler2.getSupportedResource()).thenReturn("concept");
		when(searchHandler2.getRequiredParameters()).thenReturn(new HashSet<String>(Arrays.asList("sourceName")));
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("code", "some name");
		
		SearchHandler searchHandler3 = service.getSearchHandler("concept", parameters);
		assertThat(searchHandler3, nullValue());
	}
}

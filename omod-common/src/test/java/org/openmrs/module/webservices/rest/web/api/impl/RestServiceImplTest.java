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
import java.util.Set;

import org.junit.Test;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.resource.api.SearchConfig;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.response.InvalidSearchException;

public class RestServiceImplTest {
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw exception if no handler with id
	 */
	@Test
	public void getSearchHandler_shouldThrowExceptionIfNoHandlerWithId() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("none", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("Fuzzy search").withRequiredParameters("q").build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("s", "conceptByMapping");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (InvalidSearchException e) {}
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler by id if exists
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerByIdIfExists() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("Fuzzy search").withRequiredParameters("q").build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("s", "conceptByMapping");
		SearchHandler searchHandler2 = service.getSearchHandler("concept", parameters);
		assertThat(searchHandler2, is(searchHandler));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies throw ambiguous exception if case 1
	 */
	@Test
	public void getSearchHandler_shouldThrowAmbiguousExceptionIfCase1() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName", "code").build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig.Builder("conceptByMapping2", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName", "code").build()).build();
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		parameters.put("code", "some code");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (InvalidSearchException e) {}
	}
	
	public Set<SearchQuery> newParameters(SearchQuery... parameters) {
		return new HashSet<SearchQuery>(Arrays.asList(parameters));
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return handler if case 2
	 */
	@Test
	public void getSearchHandler_shouldReturnHandlerIfCase2() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		            .build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig.Builder("conceptByMapping2", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").build()).build();
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
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
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		            .build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig.Builder("conceptByMapping2", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").build()).build();
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		
		try {
			service.getSearchHandler("concept", parameters);
			fail();
		}
		catch (InvalidSearchException e) {}
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if too few parameters
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfTooFewParameters() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").withOptionalParameters("code")
		            .build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		SearchHandler searchHandler2 = mock(SearchHandler.class);
		SearchConfig searchConfig2 = new SearchConfig.Builder("conceptByMapping2", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").build()).build();
		when(searchHandler2.getSearchConfig()).thenReturn(searchConfig2);
		service.addSupportedSearchHandler(searchHandler2);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("code", "some name");
		
		SearchHandler searchHandler3 = service.getSearchHandler("concept", parameters);
		assertThat(searchHandler3, nullValue());
	}
	
	/**
	 * @see RestServiceImpl#getSearchHandler(String,Map)
	 * @verifies return null if resource does not match
	 */
	@Test
	public void getSearchHandler_shouldReturnNullIfResourceDoesNotMatch() throws Exception {
		RestServiceImpl service = new RestServiceImpl();
		
		SearchHandler searchHandler = mock(SearchHandler.class);
		SearchConfig searchConfig = new SearchConfig.Builder("conceptByMapping", "concept", "1.8.*").withSearchQueries(
		    new SearchQuery.Builder("description").withRequiredParameters("sourceName").build()).build();
		when(searchHandler.getSearchConfig()).thenReturn(searchConfig);
		service.addSupportedSearchHandler(searchHandler);
		
		RestUtil.disableContext(); //to avoid a Context call
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sourceName", "some name");
		
		SearchHandler searchHandler2 = service.getSearchHandler("nonexistingresource", parameters);
		assertThat(searchHandler2, nullValue());
	}
}

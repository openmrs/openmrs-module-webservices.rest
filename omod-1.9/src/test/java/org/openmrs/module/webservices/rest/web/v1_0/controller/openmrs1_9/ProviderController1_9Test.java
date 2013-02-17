package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.Provider;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.test.Rest1_9TestConstants;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;

public class ProviderController1_9Test extends BaseCrudControllerTest {
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest1_9TestConstants.TEST_DATASET);
	}
	
	/**
	 * @see ProviderController#createProvider(SimpleObject,WebRequest)
	 * @verifies create a new Provider
	 */
	@Test
	public void createProvider_shouldCreateANewProvider() throws Exception {
		int before = Context.getProviderService().getAllProviders().size();
		String json = "{ \"person\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"identifier\":\"abc123ez\" }";
		
		handle(newPostRequest(getURI(), json));
		Assert.assertEquals(before + 1, Context.getProviderService().getAllProviders().size());
	}
	
	/**
	 * @see ProviderController#updateProvider(Provider,SimpleObject,WebRequest)
	 * @verifies should fail when changing a person property on a Provider
	 */
	@Test(expected = ConversionException.class)
	public void updateProvider_shouldFailWhenChangingAPersonPropertyOnAProvider() throws Exception {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String json = "{\"birthdate\":\"" + df.format(now) + "\"}";
		
		handle(newPostRequest(getURI() + "/" + Rest1_9TestConstants.PROVIDER_UUID, json));
	}
	
	/**
	 * @see ProviderController#voidProvider(Provider,String,WebRequest)
	 * @verifies void a Provider
	 */
	@Test
	public void voidProvider_shouldRetireAProvider() throws Exception {
		Provider pat = Context.getProviderService().getProvider(2);
		Assert.assertFalse(pat.isRetired());
		
		MockHttpServletRequest request = request(RequestMethod.DELETE, getURI() + "/" + Rest1_9TestConstants.PROVIDER_UUID);
		request.addParameter("reason", "unit test");
		handle(request);
		
		pat = Context.getProviderService().getProvider(2);
		Assert.assertTrue(pat.isRetired());
		Assert.assertEquals("unit test", pat.getRetireReason());
	}
	
	/**
	 * @see ProviderController#findProviders(String,WebRequest,HttpServletResponse)
	 * @verifies return no results if there are no matching Providers
	 */
	@Test
	public void findProviders_shouldReturnNoResultsIfThereAreNoMatchingProviders() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI());
		request.addParameter("q", "zzzznobody");
		
		List<?> results = (List<?>) deserialize(handle(request)).get("results");
		Assert.assertEquals(0, results.size());
	}
	
	/**
	 * @see ProviderController#findProviders(String,WebRequest,HttpServletResponse)
	 * @verifies find matching Providers
	 */
	@Test
	public void findProviders_shouldFindMatchingProviders() throws Exception {
		MockHttpServletRequest request = newGetRequest(getURI());
		request.addParameter("q", "Hornblower");
		
		List<?> results = (List<?>) deserialize(handle(request)).get("results");
		Assert.assertEquals(1, results.size());
		Object result = results.get(0);
		Assert.assertEquals(Rest1_9TestConstants.PROVIDER_UUID, PropertyUtils.getProperty(result, "uuid"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getURI()
	 */
	@Override
	public String getURI() {
		return "provider";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getUuid()
	 */
	@Override
	public String getUuid() {
		return Rest1_9TestConstants.PROVIDER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return Context.getProviderService().getAllProviders().size();
	}
}

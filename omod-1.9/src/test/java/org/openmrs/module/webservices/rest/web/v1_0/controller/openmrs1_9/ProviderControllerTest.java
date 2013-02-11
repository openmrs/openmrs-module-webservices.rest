package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_9;

import org.junit.Before;
import org.openmrs.module.webservices.rest.test.Rest19ExtTestConstants;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest;

public class ProviderControllerTest extends BaseCrudControllerTest {
	
	@Before
	public void before() throws Exception {
		executeDataSet(Rest19ExtTestConstants.TEST_DATASET);
	}
	
//	/**
//	 * @see ProviderController#createProvider(SimpleObject,WebRequest)
//	 * @verifies create a new Provider
//	 */
//	@Test
//	public void createProvider_shouldCreateANewProvider() throws Exception {
//		int before = Context.getProviderService().getAllProviders().size();
//		String json = "{ \"person\": \"da7f524f-27ce-4bb2-86d6-6d1d05312bd5\", \"identifier\":\"abc123ez\" }";
//		SimpleObject post = new ObjectMapper().readValue(json, SimpleObject.class);
//		Object newProvider = new ProviderController().create(post, new MockHttpServletRequest(),
//		    new MockHttpServletResponse());
//		Assert.assertEquals(before + 1, Context.getProviderService().getAllProviders().size());
//	}
//	
//	/**
//	 * @see ProviderController#getProvider(Provider,WebRequest)
//	 * @verifies get a default representation of a Provider
//	 */
//	@Test
//	public void getProvider_shouldGetADefaultRepresentationOfAProvider() throws Exception {
//		Object result = new ProviderController()
//		        .retrieve(Rest19ExtTestConstants.PROVIDER_UUID, new MockHttpServletRequest());
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifier"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
//		Assert.assertNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see ProviderController#getProvider(String,WebRequest)
//	 * @verifies get a full representation of a Provider
//	 */
//	@Test
//	public void getProvider_shouldGetAFullRepresentationOfAProvider() throws Exception {
//		MockHttpServletRequest req = new MockHttpServletRequest();
//		req.addParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION, RestConstants.REPRESENTATION_FULL);
//		Object result = new ProviderController().retrieve(Rest19ExtTestConstants.PROVIDER_UUID, req);
//		Assert.assertNotNull(result);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "identifier"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "person"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "auditInfo"));
//	}
//	
//	/**
//	 * @see ProviderController#updateProvider(Provider,SimpleObject,WebRequest)
//	 * @verifies should fail when changing a person property on a Provider
//	 */
//	@Test(expected = ConversionException.class)
//	public void updateProvider_shouldFailWhenChangingAPersonPropertyOnAProvider() throws Exception {
//		Date now = new Date();
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleObject post = new ObjectMapper().readValue("{\"birthdate\":\"" + df.format(now) + "\"}", SimpleObject.class);
//		new ProviderController().update(Rest19ExtTestConstants.PROVIDER_UUID, post, new MockHttpServletRequest(),
//		    new MockHttpServletResponse());
//	}
//	
//	/**
//	 * @see ProviderController#voidProvider(Provider,String,WebRequest)
//	 * @verifies void a Provider
//	 */
//	@Test
//	public void voidProvider_shouldRetireAProvider() throws Exception {
//		Provider pat = Context.getProviderService().getProvider(2);
//		Assert.assertFalse(pat.isRetired());
//		new ProviderController().delete(Rest19ExtTestConstants.PROVIDER_UUID, "unit test", new MockHttpServletRequest(),
//		    new MockHttpServletResponse());
//		pat = Context.getProviderService().getProvider(2);
//		Assert.assertTrue(pat.isRetired());
//		Assert.assertEquals("unit test", pat.getRetireReason());
//	}
//	
//	/**
//	 * @see ProviderController#findProviders(String,WebRequest,HttpServletResponse)
//	 * @verifies return no results if there are no matching Providers
//	 */
//	@Test
//	public void findProviders_shouldReturnNoResultsIfThereAreNoMatchingProviders() throws Exception {
//		List<?> results = (List<?>) new ProviderController().search("zzzznobody", new MockHttpServletRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(0, results.size());
//	}
//	
//	/**
//	 * @see ProviderController#findProviders(String,WebRequest,HttpServletResponse)
//	 * @verifies find matching Providers
//	 */
//	@Test
//	public void findProviders_shouldFindMatchingProviders() throws Exception {
//		List<?> results = (List<?>) new ProviderController().search("Hornblower", new MockHttpServletRequest(),
//		    new MockHttpServletResponse()).get("results");
//		Assert.assertEquals(1, results.size());
//		Object result = results.get(0);
//		Assert.assertEquals(Rest19ExtTestConstants.PROVIDER_UUID, PropertyUtils.getProperty(result, "uuid"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "links"));
//		Assert.assertNotNull(PropertyUtils.getProperty(result, "display"));
//	}
//	
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
		return Rest19ExtTestConstants.PROVIDER_UUID;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.v1_0.controller.BaseCrudControllerTest#getAllCount()
	 */
	@Override
	public long getAllCount() {
		return 2;
	}
}

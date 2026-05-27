/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

public class ConversionUtilPrivilegeTest extends BaseModuleWebContextSensitiveTest {

	private static final String FAKE_PRIVILEGE = "Test-Privilege-Nobody-Has-9f8c7d6e";

	@AfterEach
	public void resetConverterCache() {
		ConversionUtil.clearCache();
	}

	@Test
	public void convertToRepresentation_shouldReturnPrivilegeDeniedWhenAnyListElementIsDenied() {
		Context.logout();
		List<TestDelegate> input = Arrays.asList(new TestDelegate("a"), new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new DeniedTestResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldRetainAllowedListElements() {
		List<TestDelegate> input = Arrays.asList(new TestDelegate("a"), new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new AllowedTestResource());

		List<?> resultList = (List<?>) result;
		assertEquals(2, resultList.size());
		assertEquals("a", ((SimpleObject) resultList.get(0)).get("uuid"));
		assertEquals("b", ((SimpleObject) resultList.get(1)).get("uuid"));
	}

	@Test
	public void convertToRepresentation_shouldReturnPrivilegeDeniedWhenAnyElementOfMixedListIsDenied() {
		Context.logout();
		List<TestDelegate> input = Arrays.asList(new TestDelegate("a"), new TestDelegate("b"), new TestDelegate("c"),
		    new TestDelegate("d"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new AlternatingTestResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldReturnPrivilegeDeniedWhenAnyMapValueIsDenied() throws Exception {
		Context.logout();
		seedConverterCache(TestDelegate.class, new DeniedTestResource());

		Map<String, TestDelegate> input = new LinkedHashMap<>();
		input.put("k1", new TestDelegate("a"));
		input.put("k2", new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT);

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldRetainAllowedMapValues() throws Exception {
		seedConverterCache(TestDelegate.class, new AllowedTestResource());

		Map<String, TestDelegate> input = new LinkedHashMap<>();
		input.put("k1", new TestDelegate("a"));
		input.put("k2", new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT);

		SimpleObject resultMap = (SimpleObject) result;
		assertEquals(2, resultMap.size());
		assertEquals("a", ((SimpleObject) resultMap.get("k1")).get("uuid"));
		assertEquals("b", ((SimpleObject) resultMap.get("k2")).get("uuid"));
	}

	@Test
	public void getPropertyWithRepresentation_shouldReturnPrivilegeDeniedWhenAnyCollectionPropertyElementIsDenied() throws Exception {
		Context.logout();
		seedConverterCache(TestDelegate.class, new DeniedTestResource());

		TestParent bean = new TestParent();
		bean.setChildren(Arrays.asList(new TestDelegate("a"), new TestDelegate("b")));

		Object result = ConversionUtil.getPropertyWithRepresentation(bean, "children", Representation.DEFAULT);

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldStillReturnPrivilegeDeniedForSingleDeniedObject() {
		Context.logout();

		Object result = ConversionUtil.convertToRepresentation(new TestDelegate("a"), Representation.DEFAULT,
		    new DeniedTestResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldNotMutateSourceCollection() {
		Context.logout();
		List<TestDelegate> input = new ArrayList<>(Arrays.asList(new TestDelegate("a"), new TestDelegate("b")));

		ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new DeniedTestResource());

		assertEquals(2, input.size());
		assertEquals("a", input.get(0).uuid);
		assertEquals("b", input.get(1).uuid);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void seedConverterCache(Class<?> clazz, Converter<?> converter) throws Exception {
		Field cacheField = ConversionUtil.class.getDeclaredField("converterCache");
		cacheField.setAccessible(true);
		ConcurrentMap cache = (ConcurrentMap) cacheField.get(null);
		cache.put(clazz, converter);
	}

	public record TestDelegate(String uuid) {

	}

	public static class TestParent {

		private List<TestDelegate> children;

		// Accessed reflectively by PropertyUtils.getProperty in getPropertyWithRepresentation.
		@SuppressWarnings("unused")
		public List<TestDelegate> getChildren() {
			return children;
		}

		public void setChildren(List<TestDelegate> children) {
			this.children = children;
		}
	}

	public abstract static class StubTestResource extends BaseDelegatingResource<TestDelegate> {

		@Override
		public SimpleObject asRepresentation(TestDelegate delegate, Representation rep) {
			SimpleObject obj = new SimpleObject();
			obj.put("uuid", delegate.uuid);
			return obj;
		}

		@Override
		public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
			return null;
		}

		@Override
		public TestDelegate getByUniqueId(String uniqueId) {
			return null;
		}

		@Override
		protected void delete(TestDelegate delegate, String reason, RequestContext context) {
		}

		@Override
		public void purge(TestDelegate delegate, RequestContext context) {
		}

		@Override
		public TestDelegate newDelegate() {
			return null;
		}

		@Override
		public TestDelegate save(TestDelegate delegate) {
			return delegate;
		}

		@Override
		public String getUri(Object delegate) {
			return "";
		}
	}

	public static class DeniedTestResource extends StubTestResource {

		@Override
		public String getRequiredGetPrivilege() {
			return FAKE_PRIVILEGE;
		}
	}

	public static class AllowedTestResource extends StubTestResource {
	}

	public static class AlternatingTestResource extends StubTestResource {

		private int counter = 0;

		@Override
		public String getRequiredGetPrivilege() {
			return (counter++ % 2 == 1) ? FAKE_PRIVILEGE : null;
		}
	}
}

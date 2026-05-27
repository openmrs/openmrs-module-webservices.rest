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

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openmrs.api.ConceptNameType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.representation.CustomRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.web.test.jupiter.BaseModuleWebContextSensitiveTest;

public class ConversionUtilTest extends BaseModuleWebContextSensitiveTest {
	
	/**
	 * @see ConversionUtil#convert(Object,Type)
	 * @verifies String to Date conversion for multiple formatted date/dateTime strings
	 */
	@Test
	public void convert_shouldReturnEqualsDateFromString() throws Exception {
		Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").parse("2011-05-01T00:00:00.000");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.setTimeZone(TimeZone.getDefault());
		Date expected = cal.getTime();
		String[] dateFormats = { "2011-05-01", "2011-05-01 00:00:00", "2011-05-01T00:00:00.000", "2011-05-01T00:00:00.000" };
		for (int i = 0; i < dateFormats.length; i++) {
			Date result = (Date) ConversionUtil.convert(dateFormats[i], Date.class);
			Assertions.assertEquals(result, expected);
		}
	}
	
	/**
	 * @see ConversionUtil#convert(Object,Type)
	 * @verifies String to Date conversion for multiple formatted date/dateTime strings having
	 *           timezone
	 */
	@Test
	public void convert_shouldReturnCorrectDateWhenParsingStringHavingTimeZone() throws Exception {
		Date expectedDate1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse("2016-01-12T06:00:00+0530");
		//Added to check against more ISO8601 format dates im 'dates2' array
		Date expectedDate2 = (Date) ConversionUtil.convert("2014-02-20T11:00:00.000-0500", Date.class);
		
		String[] dates1 = { "2016-01-12T06:00:00+05:30", "2016-01-12T06:00:00+0530" };
		String[] dates2 = { "2014-02-20T11:00:00.000-05:00", "2014-02-20T11:00:00.000-05" };
		
		for (String date : dates1) {
			Date actualDate = (Date) ConversionUtil.convert(date, Date.class);
			Assertions.assertEquals(expectedDate1, actualDate);
		}
		
		for (String date : dates2) {
			Date actualDate = (Date) ConversionUtil.convert(date, Date.class);
			Assertions.assertEquals(expectedDate2, actualDate);
		}
	}
	
	/**
	 * @see ConversionUtil#convert(Object,Type)
	 * @verifies String to Date conversion by assert false for date mismatches
	 */
	@Test
	public void convert_shouldReturnFalseOnIncorrectDateFromString() throws Exception {
		Date expected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse("2011-05-01T00:00:00.000+0530");
		String[] dateFormats = { "2011-05-01T00:00:00.000+0200", "2012-05-01T00:00:00.000" };
		for (int i = 0; i < dateFormats.length; i++) {
			Date result = (Date) ConversionUtil.convert(dateFormats[i], Date.class);
			Assertions.assertTrue(result != expected);
		}
	}
	
	/**
	 * @see ConversionUtil#convert(Object,Type)
	 * @verifies String format and its representation are equal
	 */
	@Test
	public void convertToRepresentation_shouldReturnSameStringForToday() throws Exception {
		Date today = new Date();
		String expected = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(today);
		String result = (String) ConversionUtil.convertToRepresentation(today, Representation.REF);
		Assertions.assertEquals(result, expected);
	}
	
	/**
	 * @see {@link ConversionUtil#convert(Object,Type)}
	 */
	@Test
	public void convert_shouldSConvertStringsToEnumsValues() throws Exception {
		Object conceptNameType = ConversionUtil.convert("FULLY_SPECIFIED", ConceptNameType.class);
		Assertions.assertNotNull(conceptNameType);
		Assertions.assertTrue(conceptNameType.getClass().isAssignableFrom(ConceptNameType.class));
	}
	
	/**
	 * @see {@link ConversionUtil#convert(Object,Type)}
	 */
	@Test
	public void convert_shouldConvertStringsToLocales() throws Exception {
		Object locale = ConversionUtil.convert("en", Locale.class);
		Assertions.assertNotNull(locale);
		Assertions.assertTrue(locale.getClass().isAssignableFrom(Locale.class));
	}
	
	/**
	 * @see {@link ConversionUtil#convert(Object,Type)}
	 * @verifies convert to an array
	 */
	@Test
	public void convert_shouldConvertToAnArray() throws Exception {
		List<String> input = Arrays.asList("en", "fr");
		Locale[] converted = (Locale[]) ConversionUtil.convert(input, Locale[].class);
		assertThat(converted.length, is(2));
		assertThat(converted[0], is(Locale.ENGLISH));
		assertThat(converted[1], is(Locale.FRENCH));
	}
	
	/**
	 * @see {@link ConversionUtil#convert(Object,Type)}
	 * @verifies convert to a class
	 */
	@Test
	public void convert_shouldConvertToAClass() throws Exception {
		String input = "java.lang.String";
		Class converted = (Class) ConversionUtil.convert(input, Class.class);
		Assertions.assertTrue(converted.isAssignableFrom(String.class));
	}
	
	@Test
	public void convert_shouldConvertSimpleObjectToCustomRepresentation() throws Exception {
		
		SimpleObject child = new SimpleObject();
		child.put("child_key_1", "child_val_1");
		child.put("child_key_2", "child_val_2");
		SimpleObject parent = new SimpleObject();
		parent.put("parent_key_1", child);
		parent.put("parent_key_2", "parent_val_2");
		
		Object o = ConversionUtil.convertToRepresentation(parent, new CustomRepresentation("parent_key_1:(child_key_1)"));
		
		SimpleObject expectedChild = new SimpleObject();
		expectedChild.put("child_key_1", "child_val_1");
		SimpleObject expectedParent = new SimpleObject();
		expectedParent.put("parent_key_1", expectedChild);
		
		assertEquals(expectedParent, o);
	}
	
	public void convert_shouldConvertIntToDouble() throws Exception {
		assertThat((Double) ConversionUtil.convert(5, Double.class), is(5d));
	}
	
	public void convert_shouldConvertDoubleToInt() throws Exception {
		assertThat((Integer) ConversionUtil.convert(5d, Integer.class), is(5));
	}
	
	/**
	 * @verifies resolve TypeVariables to actual type
	 * @see ConversionUtil#convert(Object, java.lang.reflect.Type)
	 */
	@Test
	public void convert_shouldResolveTypeVariablesToActualType() throws Exception {
		ChildGenericType_Int i = new ChildGenericType_Int();
		Method setter = PropertyUtils.getPropertyDescriptor(i, "value").getWriteMethod();
		
		Object result = ConversionUtil.convert("25", setter.getGenericParameterTypes()[0], i);
		
		Assertions.assertNotNull(result);
		Assertions.assertEquals(25, result);
	}
	
	/**
	 * @verifies return the actual type if defined on the parent class
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldReturnTheActualTypeIfDefinedOnTheParentClass() throws Exception {
		ChildGenericType_Int i = new ChildGenericType_Int();
		ChildGenericType_String s = new ChildGenericType_String();
		ChildGenericType_Temp t = new ChildGenericType_Temp();
		
		Method setter = PropertyUtils.getPropertyDescriptor(i, "value").getWriteMethod();
		Type type = ConversionUtil.getTypeVariableClass(ChildGenericType_Int.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Integer.class, type);
		
		setter = PropertyUtils.getPropertyDescriptor(s, "value").getWriteMethod();
		type = ConversionUtil.getTypeVariableClass(ChildGenericType_String.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(String.class, type);
		
		setter = PropertyUtils.getPropertyDescriptor(t, "value").getWriteMethod();
		type = ConversionUtil.getTypeVariableClass(ChildGenericType_Temp.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Temp.class, type);
	}
	
	/**
	 * @verifies return the actual type if defined on the grand-parent class
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldReturnTheActualTypeIfDefinedOnTheGrandparentClass() throws Exception {
		GrandchildGenericType_Int i = new GrandchildGenericType_Int();
		GreatGrandchildGenericType_Int i2 = new GreatGrandchildGenericType_Int();
		
		Method setter = PropertyUtils.getPropertyDescriptor(i, "value").getWriteMethod();
		Type type = ConversionUtil.getTypeVariableClass(GrandchildGenericType_Int.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Integer.class, type);
		
		setter = PropertyUtils.getPropertyDescriptor(i2, "value").getWriteMethod();
		type = ConversionUtil.getTypeVariableClass(GreatGrandchildGenericType_Int.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Integer.class, type);
	}
	
	/**
	 * @verifies return null when actual type cannot be found
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldReturnNullWhenActualTypeCannotBeFound() throws Exception {
		GrandchildGenericType_Int i = new GrandchildGenericType_Int();
		
		Method setter = PropertyUtils.getPropertyDescriptor(i, "value").getWriteMethod();
		Type type = ConversionUtil.getTypeVariableClass(Temp.class, (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNull(type);
	}
	
	/**
	 * @verifies return the correct actual type if there are multiple generic types
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldReturnTheCorrectActualTypeIfThereAreMultipleGenericTypes() throws Exception {
		ChildMultiGenericType i = new ChildMultiGenericType();
		
		Method setter = PropertyUtils.getPropertyDescriptor(i, "first").getWriteMethod();
		Type type = ConversionUtil.getTypeVariableClass(ChildMultiGenericType.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Integer.class, type);
		
		setter = PropertyUtils.getPropertyDescriptor(i, "second").getWriteMethod();
		type = ConversionUtil.getTypeVariableClass(ChildMultiGenericType.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(String.class, type);
		
		setter = PropertyUtils.getPropertyDescriptor(i, "third").getWriteMethod();
		type = ConversionUtil.getTypeVariableClass(ChildMultiGenericType.class,
		    (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		
		Assertions.assertNotNull(type);
		Assertions.assertEquals(Temp.class, type);
	}
	
	/**
	 * @verifies throw IllegalArgumentException when instance class is null
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldThrowIllegalArgumentExceptionWhenInstanceClassIsNull() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			GrandchildGenericType_Int i = new GrandchildGenericType_Int();
		
			Method setter = PropertyUtils.getPropertyDescriptor(i, "value").getWriteMethod();
			Type type = ConversionUtil.getTypeVariableClass(null, (TypeVariable<?>) setter.getGenericParameterTypes()[0]);
		});
	}
	
	/**
	 * @verifies throw IllegalArgumentException when typeVariable is null
	 * @see ConversionUtil#getTypeVariableClass(Class, java.lang.reflect.TypeVariable)
	 */
	@Test
	public void getTypeVariableClass_shouldThrowIllegalArgumentExceptionWhenTypeVariableIsNull() throws Exception {
		assertThrows(IllegalArgumentException.class, () -> {
			ConversionUtil.getTypeVariableClass(Temp.class, null);
		});
	}
	
	public abstract class BaseGenericType<T> {
		
		private T value;
		
		public T getValue() {
			return value;
		}
		
		public void setValue(T value) {
			this.value = value;
		}
	}
	
	public abstract class BaseMultiGenericType<F, S, T> {
		
		private F first;
		
		private S second;
		
		private T third;
		
		public F getFirst() {
			return first;
		}
		
		public void setFirst(F first) {
			this.first = first;
		}
		
		public S getSecond() {
			return second;
		}
		
		public void setSecond(S second) {
			this.second = second;
		}
		
		public T getThird() {
			return third;
		}
		
		public void setThird(T third) {
			this.third = third;
		}
	}
	
	public class Temp {}
	
	public class ChildGenericType_Int extends BaseGenericType<Integer> {}
	
	public class ChildGenericType_String extends BaseGenericType<String> {}
	
	public class ChildGenericType_Temp extends BaseGenericType<Temp> {}
	
	public class GrandchildGenericType_Int extends ChildGenericType_Int {}
	
	public class GreatGrandchildGenericType_Int extends GrandchildGenericType_Int {}
	
	public class ChildMultiGenericType extends BaseMultiGenericType<Integer, String, Temp> {}

	// ---- RESTWS-1035: PRIVILEGE_DENIED leakage into Collection/Map element conversions ----

	private static final String FAKE_PRIVILEGE = "Test-Privilege-Nobody-Has-9f8c7d6e";

	@AfterEach
	public void resetConverterCache() {
		ConversionUtil.clearCache();
	}

	@Test
	public void convertToRepresentation_shouldReturnPrivilegeDeniedWhenAnyListElementIsDenied() throws Exception {
		Context.logout();
		List<TestDelegate> input = Arrays.asList(new TestDelegate("a"), new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new DeniedTestResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldRetainAllowedListElements() throws Exception {
		List<TestDelegate> input = Arrays.asList(new TestDelegate("a"), new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT, new AllowedTestResource());

		List<?> resultList = (List<?>) result;
		assertEquals(2, resultList.size());
		assertEquals("a", ((SimpleObject) resultList.get(0)).get("uuid"));
		assertEquals("b", ((SimpleObject) resultList.get(1)).get("uuid"));
	}

	@Test
	public void convertToRepresentation_shouldReturnPrivilegeDeniedWhenAnyElementOfMixedListIsDenied() throws Exception {
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

		Map<String, TestDelegate> input = new LinkedHashMap<String, TestDelegate>();
		input.put("k1", new TestDelegate("a"));
		input.put("k2", new TestDelegate("b"));

		Object result = ConversionUtil.convertToRepresentation(input, Representation.DEFAULT);

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldRetainAllowedMapValues() throws Exception {
		seedConverterCache(TestDelegate.class, new AllowedTestResource());

		Map<String, TestDelegate> input = new LinkedHashMap<String, TestDelegate>();
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
	public void convertToRepresentation_shouldStillReturnPrivilegeDeniedForSingleDeniedObject() throws Exception {
		Context.logout();

		Object result = ConversionUtil.convertToRepresentation(new TestDelegate("a"), Representation.DEFAULT,
		    new DeniedTestResource());

		assertSame(ConversionUtil.PRIVILEGE_DENIED, result);
	}

	@Test
	public void convertToRepresentation_shouldNotMutateSourceCollection() throws Exception {
		Context.logout();
		List<TestDelegate> input = new ArrayList<TestDelegate>(Arrays.asList(new TestDelegate("a"), new TestDelegate("b")));

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

	public static class TestDelegate {

		public final String uuid;

		public TestDelegate(String uuid) {
			this.uuid = uuid;
		}
	}

	public static class TestParent {

		private List<TestDelegate> children;

		public List<TestDelegate> getChildren() {
			return children;
		}

		public void setChildren(List<TestDelegate> children) {
			this.children = children;
		}
	}

	public static abstract class StubTestResource extends BaseDelegatingResource<TestDelegate> {

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
		// inherits null from BaseDelegatingResource.getRequiredGetPrivilege()
	}

	public static class AlternatingTestResource extends StubTestResource {

		// Alternates allow/deny per element so we can test a list with both kept and dropped entries
		// using a single specificConverter (the recursion preserves specificConverter, so all elements
		// flow through the same resource instance).
		private int counter = 0;

		@Override
		public String getRequiredGetPrivilege() {
			return (counter++ % 2 == 1) ? FAKE_PRIVILEGE : null;
		}
	}
}

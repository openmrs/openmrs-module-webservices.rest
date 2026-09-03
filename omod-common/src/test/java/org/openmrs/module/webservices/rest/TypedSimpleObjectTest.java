/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TypedSimpleObject} verifying that it behaves identically
 * to {@link SimpleObject} for basic map operations.
 */
public class TypedSimpleObjectTest {
	
	@Test
	public void defaultConstructor_shouldCreateEmptyMap() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		assertTrue(obj.isEmpty());
		assertEquals(0, obj.size());
	}
	
	@Test
	public void initialCapacityConstructor_shouldCreateEmptyMap() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>(16);
		assertTrue(obj.isEmpty());
		assertEquals(0, obj.size());
	}
	
	@Test
	public void put_shouldStoreAndRetrieveValue() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		obj.put("name", "Test Patient");
		assertEquals("Test Patient", obj.get("name"));
		assertEquals(1, obj.size());
	}
	
	@Test
	public void get_shouldReturnNullForMissingKey() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		assertNull(obj.get("nonexistent"));
	}
	
	@Test
	public void add_shouldSupportMethodChaining() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		TypedSimpleObject<?> returned = obj.add("uuid", "abc-123").add("display", "Test");
		
		// add() should return the same instance for chaining
		assertSame(obj, returned);
		assertEquals("abc-123", obj.get("uuid"));
		assertEquals("Test", obj.get("display"));
		assertEquals(2, obj.size());
	}
	
	@Test
	public void removeProperty_shouldRemoveKeyAndSupportChaining() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		obj.add("a", 1).add("b", 2).add("c", 3);
		
		TypedSimpleObject<?> returned = obj.removeProperty("b");
		
		assertSame(obj, returned);
		assertEquals(2, obj.size());
		assertFalse(obj.containsKey("b"));
		assertEquals(1, (int) obj.<Integer> get("a"));
		assertEquals(3, (int) obj.<Integer> get("c"));
	}
	
	@Test
	public void shouldBeAssignableToSimpleObject() {
		TypedSimpleObject<?> typed = new TypedSimpleObject<>();
		typed.add("key", "value");
		
		// TypedSimpleObject must be assignable to SimpleObject for backward compatibility
		SimpleObject simple = typed;
		assertEquals("value", simple.get("key"));
	}
	
	@Test
	public void shouldHandleNestedSimpleObjects() {
		TypedSimpleObject<?> outer = new TypedSimpleObject<>();
		SimpleObject inner = new SimpleObject();
		inner.add("nested", true);
		
		outer.add("child", inner);
		
		SimpleObject retrieved = outer.get("child");
		assertEquals(true, retrieved.get("nested"));
	}
	
	@Test
	public void shouldPreserveInsertionOrder() {
		TypedSimpleObject<?> obj = new TypedSimpleObject<>();
		obj.add("z", 1).add("a", 2).add("m", 3);
		
		// LinkedHashMap preserves insertion order
		String[] keys = obj.keySet().toArray(new String[0]);
		assertEquals("z", keys[0]);
		assertEquals("a", keys[1]);
		assertEquals("m", keys[2]);
	}
}

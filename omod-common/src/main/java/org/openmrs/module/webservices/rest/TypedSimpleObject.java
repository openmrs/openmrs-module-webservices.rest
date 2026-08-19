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

/**
 * A type-parameterized extension of {@link SimpleObject} introduced as part of
 * <a href="https://issues.openmrs.org/browse/RESTWS-1040">RESTWS-1040</a> /
 * <a href="https://issues.openmrs.org/browse/RESTWS-1041">RESTWS-1041</a> to
 * eventually carry accurate OpenAPI / Swagger type information for REST resource
 * representations.
 *
 * <h3>Why this class exists</h3>
 * <p>
 * {@code SimpleObject} (which extends {@code LinkedHashMap<String, Object>}) is the
 * universal return type for all REST resource handlers today. This works at runtime
 * but prevents tooling (OpenAPI generators, IDE inspections, static analysis) from
 * knowing <em>which</em> domain type a given map actually represents.
 * </p>
 * <p>
 * {@code TypedSimpleObject<T>} adds a generic type parameter {@code <T>} so that
 * resource methods can declare, for example,
 * {@code TypedSimpleObject<Patient>} instead of plain {@code SimpleObject}.
 * The parameter is erased at runtime and has <strong>no effect on serialization or
 * map behaviour</strong> — it exists solely to improve compile-time documentation
 * and to enable future OpenAPI schema generation.
 * </p>
 *
 * <h3>Backward compatibility</h3>
 * <p>
 * This class intentionally extends {@link SimpleObject} (not
 * {@code LinkedHashMap} directly) so that every {@code TypedSimpleObject} is
 * assignment-compatible with {@code SimpleObject}. Modules that still consume
 * the old {@code SimpleObject}-based API will continue to work unchanged
 * during the REST 4.x migration window.
 * </p>
 *
 * <h3>Current usage (Phase 1)</h3>
 * <p>
 * In this initial phase the type parameter is {@code <?>} in most call sites
 * ({@code TypedSimpleObject<?>}). Follow-up tickets will progressively replace
 * the wildcard with concrete domain types (e.g.&nbsp;{@code Patient},
 * {@code Encounter}) as individual resources are migrated.
 * </p>
 *
 * @param <T> the OpenMRS domain type this object represents; currently unused
 *            (erased) at most call sites and present only for compile-time
 *            documentation purposes
 * @see SimpleObject
 * @since 2.46.0
 */
public class TypedSimpleObject<T> extends SimpleObject {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates an empty {@code TypedSimpleObject}.
	 */
	public TypedSimpleObject() {
	}
	
	/**
	 * Creates an empty {@code TypedSimpleObject} with the specified initial capacity.
	 *
	 * @param initialCapacity the initial capacity of the underlying map
	 */
	public TypedSimpleObject(int initialCapacity) {
		super(initialCapacity);
	}
	
	/**
	 * Puts a property in this map and returns the map itself (for chained method
	 * calls), preserving the concrete {@code TypedSimpleObject} return type.
	 *
	 * @param key   the property name
	 * @param value the property value
	 * @return this instance, for method chaining
	 */
	@Override
	public TypedSimpleObject<T> add(String key, Object value) {
		put(key, value);
		return this;
	}
	
	/**
	 * Removes a property from this map and returns the map itself (for chained
	 * method calls), preserving the concrete {@code TypedSimpleObject} return type.
	 *
	 * @param key the property name to remove
	 * @return this instance, for method chaining
	 */
	@Override
	public TypedSimpleObject<T> removeProperty(String key) {
		remove(key);
		return this;
	}
}

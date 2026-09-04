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
 * TypedSimpleObject<T> is a subclass of {@link SimpleObject} that allows us to store the type of its underlying
 * data in the generic class {@code T}. This allows us to make better inference on the return type via reflection.
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

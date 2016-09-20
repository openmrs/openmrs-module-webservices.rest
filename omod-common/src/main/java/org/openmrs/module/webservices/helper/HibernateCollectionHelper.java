/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.helper;

import java.util.Collection;
import java.util.Comparator;

/**
 * Hibernate does not allow for replacing a collection in an attached object. The collection must be
 * updated instead. This class is used to manipulate an existing collection property when calling
 * set, by delegating to proper add and remove methods on the given instance and using a custom
 * comparator for elements.
 * 
 * @param <T>
 * @param <E>
 */
public abstract class HibernateCollectionHelper<T, E> implements Comparator<E> {
	
	protected T instance;
	
	public HibernateCollectionHelper(T instance) {
		this.instance = instance;
	}
	
	public abstract Collection<E> getAll();
	
	public abstract void add(E item);
	
	public abstract void remove(E item);
	
	public void set(Collection<E> items) {
		//delete objects which are absent in new list
		for (E oldItem : getAll()) {
			boolean found = false;
			
			for (E newItem : items) {
				if (compare(oldItem, newItem) == 0) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				remove(oldItem);
			}
		}
		
		//add objects which are absent in old list
		for (E newItem : items) {
			boolean found = false;
			for (E oldItem : getAll()) {
				if (compare(oldItem, newItem) == 0) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				add(newItem);
			}
		}
	}
}

package org.openmrs.module.webservices.rest.web.v1_0.test;

import org.openmrs.BaseOpenmrsObject;

public class GenericBase<T> extends BaseOpenmrsObject {
	private Integer id;
	private T value;

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}
}

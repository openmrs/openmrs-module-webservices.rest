package org.openmrs.module.webservices.rest.web.propertyeditor;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.api.OpenmrsService;
import org.openmrs.api.context.Context;

/**
 * You can use this to override the property editors in the OpenMRS core, that are based off of
 * primary keys instead of UUIDs.
 */
public class UuidEditor extends PropertyEditorSupport {
	
	Class<? extends OpenmrsService> serviceClass;
	
	String methodName;
	
	public UuidEditor(Class<? extends OpenmrsService> serviceClass, String methodName) {
		this.serviceClass = serviceClass;
		this.methodName = methodName;
	}
	
	@Override
	public void setAsText(String uuid) throws IllegalArgumentException {
		OpenmrsService service = Context.getService(serviceClass);
		try {
			Method method = service.getClass().getMethod(methodName, String.class);
			setValue(method.invoke(service, uuid));
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public String getAsText() {
		try {
			return (String) PropertyUtils.getProperty(getValue(), "uuid");
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
}

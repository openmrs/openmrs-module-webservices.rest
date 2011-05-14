package org.openmrs.module.webservices.rest.web.resource;

import org.apache.commons.beanutils.PropertyUtils;
import org.openmrs.module.webservices.rest.web.annotation.Resource;

@Resource("person")
public class PersonResource implements org.openmrs.module.webservices.rest.web.resource.api.Resource {

	@Override
    public String getUri(Object instance) {
	    try {
	        return "something://person/" + PropertyUtils.getProperty(instance, "uuid");
        }
        catch (Exception ex) {
        	throw new RuntimeException();
        }
    }

}

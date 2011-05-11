package org.openmrs.module.webservices.rest;

import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;

/**
 * Used as a workaround for a few places where Spring will not let you throw a checked exception
 * (for example if you want to throw {@link ObjectNotFoundException} in a property editor.
 */
public class RuntimeWrappedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public RuntimeWrappedException(Exception ex) {
		super(ex);
	}
	
}

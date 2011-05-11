package org.openmrs.module.webservices.rest.representation;

import org.openmrs.module.webservices.rest.annotation.Resource;

/**
 * A representation under which a {@link Resource} can be fetched
 */
public interface Representation {
	
	Representation DEFAULT = new DefaultRepresentation();
	
	Representation REF = new RefRepresentation();
	
	Representation FULL = new FullRepresentation();
	
}

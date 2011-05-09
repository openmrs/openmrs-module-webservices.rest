package org.openmrs.module.webservices.rest;


/**
 * Specified a representation under which a resource can be fetched
 */
public interface Representation {

	Representation DEFAULT = new NamedRepresentation(WSConstants.REPRESENTATION_DEFAULT);

}

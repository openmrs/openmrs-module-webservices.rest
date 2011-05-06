package org.openmrs.module.webservices.rest;


public class RepresentationFactory {
	
	public static final Representation DEFAULT = new NamedRepresentation("default");

	public static final Representation REF = new RefRepresentation();
	
	public static Representation get(String descriptor) {
		if (descriptor == null)
			return DEFAULT;
		if (descriptor.equals("ref"))
			return REF;
		else
			return new NamedRepresentation(descriptor);
	}

}

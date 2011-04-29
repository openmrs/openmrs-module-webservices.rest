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
package org.openmrs.module.webservices.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.OpenmrsObject;
import org.openmrs.module.webservices.rest.resource.OpenmrsResource;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.Reflect;
import org.springframework.web.context.request.WebRequest;

/**
 * Convenient helper methods for the Rest Web Services module.
 * 
 */
public class WSUtil {

	private static Log log = LogFactory.getLog(WSUtil.class);

	/**
	 * Gets the default/full/custom string out of the Accept-Type header. <br/>
	 * never returns null
	 * 
	 * @param request
	 *            the current WebRequest
	 * @return a string for how to represent the to-be-returned objects
	 */
	public static String getRepresentation(WebRequest request) {
		// TODO, research accept-type. is there potential for other values to be
		// in there?

		// TODO allow a mix of default/custom ?

		String repType = request.getHeader("Accept-Type");
		if (repType != null) {
			if (WSConstants.REPRESENTATION_DEFAULT.equalsIgnoreCase(repType)) {
				return WSConstants.REPRESENTATION_DEFAULT;
			}
			if (WSConstants.REPRESENTATION_MEDIUM.equalsIgnoreCase(repType)) {
				return WSConstants.REPRESENTATION_MEDIUM;
			}
			if (WSConstants.REPRESENTATION_FULL.equalsIgnoreCase(repType)) {
				return WSConstants.REPRESENTATION_FULL;
			}
			if (repType.startsWith(WSConstants.REPRESENTATION_CUSTOM_PREFIX)) {
				return repType.replace(
						WSConstants.REPRESENTATION_CUSTOM_PREFIX, "");
			}
		}

		// for testing, go off of a param
		//return request.getParameter("rep");

		// not sure what string they put in, lets just ignore it and return
		// default
		return WSConstants.REPRESENTATION_DEFAULT;
	}

	/**
	 * Looks in the request params to see if the user wants to restrict the
	 * number of results returned. <br/>
	 * <br/>
	 * The param should be named "limit". <br/>
	 * <br/>
	 * If the param does not exist or is not a valid integer, the results are
	 * restricted to WSConstants.MAX_RESULTS size.<br/>
	 * <br/>
	 * 
	 * @param request
	 *            the current WebRequest
	 * @return Integer number of results to restrict to
	 */
	public static Integer getLimit(WebRequest request) {
		String limitString = request.getParameter("limit");
		if (limitString != null) {
			try {
				Integer limit = Integer.valueOf(limitString);
				return limit;
			} catch (NumberFormatException e) {
				// log.info("invalid limit specified: " + limitString);
			}
		}

		return getDefaultLimit();
	}

	/**
	 * Looks up the admin defined global property for the system limit
	 * 
	 * @return Integer limit
	 * @see #getLimit(WebRequest)
	 * @see WSConstants#MAX_RESULTS_GLOBAL_PROPERTY_NAME
	 */
	public static Integer getDefaultLimit() {
		// TODO check global property
		// WSConstants.MAX_RESULTS_GLOBAL_PROPERTY_NAME;

		return WSConstants.MAX_RESULTS_DEFAULT;

	}

	/**
	 * @param <OO>
	 *            the type of object being converted
	 * @param resource
	 *            the the resource to use. If null, determines it from the
	 *            contents of <code>searchResults</code>
	 * @param searchResults
	 *            the objects to convert
	 * @param representation
	 *            the representation, if null, uses "default"
	 * @param limit
	 *            an optional limit.
	 * @return the <code>searchResults</code> converted into
	 *         {@link SimpleObject}s
	 * @throws Exception
	 * @see {@link #convert(OpenmrsResource, OpenmrsObject, String)}
	 * 
	 *      TODO: don't throw generic exception
	 */
	public <OO extends OpenmrsObject> List<SimpleObject> convertList(
			OpenmrsResource resource, List<OO> searchResults,
			String representation, Integer limit) throws Exception {

		List<SimpleObject> objects = new ArrayList<SimpleObject>();

		// TODO trim searchResults to limit size if not null

		for (OpenmrsObject obj : searchResults) {
			if (resource == null)
				resource = HandlerUtil.getPreferredHandler(
						OpenmrsResource.class, obj.getClass());

			objects.add(convert(resource, obj, representation));
		}
		return objects;
	}

	/**
	 * @see #convert(OpenmrsResource, OpenmrsObject, String)
	 */
	public SimpleObject convert(OpenmrsObject openmrsObject,
			String representation) throws Exception {

		OpenmrsResource resource = HandlerUtil.getPreferredHandler(
				OpenmrsResource.class, openmrsObject.getClass());

		return convert(resource, openmrsObject, representation);
	}

	/**
	 * Converts the given <code>openmrsObject</code> into a {@link SimpleObject}
	 * to be returned to the REST user. <br/>
	 * 
	 * TODO catch each possible exception in this method and log helpful error
	 * msgs instead of just having the method throw the generic exception
	 * 
	 * TODO: change this to use a list of strings for the rep?
	 * 
	 * @param resource
	 *            the OpenmrsResource to convert to. If null, looks up the
	 *            resource from the given OpenmrsObject
	 * @param openmrsObject
	 *            the OpenmrsObject to convert
	 * @param representation
	 *            the default/full/small/custom (if null, uses "default")
	 * @return a SimpleObject (key/value pair mapping) of the object properties
	 *         requested
	 * @throws Exception
	 */
	public SimpleObject convert(OpenmrsResource resource,
			OpenmrsObject openmrsObject, String representation)
			throws Exception {

		if (representation == null)
			representation = WSConstants.REPRESENTATION_DEFAULT;

		if (resource == null)
			resource = HandlerUtil.getPreferredHandler(OpenmrsResource.class,
					openmrsObject.getClass());

		// the object to return. adds the default link/display/uuid properties
		SimpleObject simpleObject = new SimpleObject(resource, openmrsObject);

		// if they asked for a simple rep, we're done, just return that
		if (WSConstants.REPRESENTATION_REF.equals(representation))
			return simpleObject;

		// get the properties to show on this object
		String[] propsToInclude = getPropsToInclude(resource, representation);

		// loop over each prop defined and put it on the simpleObject
		for (String prop : propsToInclude) {

			// cut out potential white space around commas
			prop = prop.trim();

			// the property field on the resource of what we're converting
			Field propertyOnResource;
			try {
				propertyOnResource = resource.getClass().getDeclaredField(prop);
			} catch (NoSuchFieldException e) {
				// the user requested a field that does not exist on the
				// resource,
				// so silently skip this
				log.debug("Skipping field: " + prop
						+ " because it does not exist on the " + resource
						+ " resource");
				continue;
			}

			// the name of the getter methods for this property
			String getterName = "get" + StringUtils.capitalize(prop);

			// first check to see if there is a getter defined on the resource,
			// maybe its a custom translation to a string or OpenmrsObject and
			// we can then end early
			Method getterOnResource = getMethod(resource.getClass(),
					getterName, openmrsObject.getClass());

			if (getterOnResource != null) {
				// e.g. if prop is "name" and a dev defined
				// "personResource.getName(Person)" then we can stop here and
				// just use that method's return value
				Object returnValue = getterOnResource.invoke(resource,
						openmrsObject);

				// turn OpenmrsObjects into Refs
				if (OpenmrsObject.class
						.isAssignableFrom(returnValue.getClass())) {

					String cascadeRep = getCascadeRep(propertyOnResource,
							representation);

					SimpleObject so = convert(openmrsObject, cascadeRep);
					simpleObject.put(prop, so);
				} else
					// if(String.class.isAssignableFrom(returnValue.getClass()))
					// everything else /should be/ strings.
					// (what special about Dates, etc?)
					simpleObject.put(prop, returnValue);
				continue;
			}

			// the user didn't define a getProperty(OpenmrsObject), so we
			// need to find openmrsObject.getProperty() magically by reflection

			// get the actual value we'll need to convert on the OpenmrsObject
			Method getterOnObject = openmrsObject.getClass().getMethod(
					getterName, (Class[]) null);
			Object propValue = getterOnObject.invoke(openmrsObject,
					(Object[]) null);

			Class propertyClass = propertyOnResource.getType();

			// now convert from OpenmrsObject into this type on the resource
			if (propertyClass.equals(SimpleObject.class)) {

				String cascadeRep = getCascadeRep(propertyOnResource,
						representation);
				SimpleObject subSimpleObject = convert(resource, openmrsObject,
						cascadeRep);
				simpleObject.put(prop, subSimpleObject);
			} else if (OpenmrsResource.class.isAssignableFrom(propertyClass)) {
				// the resource has a resource property (like AuditInfo)
				OpenmrsResource openmrsResource = (OpenmrsResource) propertyClass
						.newInstance();

				// TODO: if representation just has "prop", assume that means
				// all default properties on the resource

				// TODO: if representation has "prop.x, prop.y", assume that
				// means only those properties from the resource
				// see isCollection else if statement for implementation of it
				// and possibly creating a common method for getting the
				// strippedDownRep

				// TODO: else if cascade is one of the standard ones, find the
				// rep
				// to cascade to
				String cascadeRep = getCascadeRep(propertyOnResource,
						representation);

				SimpleObject subSimpleObject = convert(openmrsResource,
						openmrsObject, cascadeRep);
				simpleObject.put(prop, subSimpleObject);
			} else if (Reflect.isCollection(propertyClass)) {

				// the list put onto the "simpleObject" as a list
				List<Object> listofSimpleObjects = new ArrayList<Object>();

				OpenmrsObject collectionContains = isOpenmrsObjectCollection(propValue);
				if (collectionContains != null) {
					// we have an OpenmrsObject collection

					OpenmrsResource collectionResource = HandlerUtil
							.getPreferredHandler(OpenmrsResource.class,
									collectionContains.getClass());

					if (representation.contains(prop + ".")) {
						// recurse on this convert method, because the user
						// asked for something complex by putting in
						// "names.givenName, names.familyName" in the
						// representation

						// TODO: look through the representation and take out
						// everything but "prop.*" strings
						String strippedDownRep = null; // new String[] {
						// "givenName",
						// "familyName",
						// "creator"};

						// recurse on this current "convert" method.
						for (OpenmrsObject o : (Collection<OpenmrsObject>) propValue) {
							convert(collectionResource, o, strippedDownRep);
						}
					} else if (WSConstants.REPRESENTATION_FULL
							.equals(representation)
							|| WSConstants.REPRESENTATION_MEDIUM
									.equals(representation)) {

						String cascadeRep = getCascadeRep(propertyOnResource,
								representation);

						for (OpenmrsObject o : (Collection<OpenmrsObject>) propValue) {
							convert(collectionResource, o, cascadeRep);
						}
					} else {
						// the user didn't ask for anything special in the rep,
						// so they get back lists of ref simple objects by
						// default
						for (OpenmrsObject o : (Collection<OpenmrsObject>) propValue) {
							// sets uuid/link/display
							SimpleObject listMemberSimpleObject = new SimpleObject(
									collectionResource, o);
							listofSimpleObjects.add(listMemberSimpleObject);
						}
					}
				} else {
					// we just have a list of java objects, simply put their
					// string values in
					// TODO how to use conversionservice here?
					for (Object o : (Collection<Object>) propValue) {
						listofSimpleObjects.add(o);
					}
				}
				simpleObject.put(prop, listofSimpleObjects);

			} else {
				// we just have some of java object, put in its toString value
				// TODO use conversionservice?
				simpleObject.put(prop, propValue);
			}

		}

		return simpleObject;
	}

	private String getCascadeRep(Field propertyOnResource, String representation) {

		// TODO: rep could be empty or null here, be sure to check for that

		// TODO: look up the WSCascade annotation on the given field
		// and return the representation that we should cascade as

		return WSConstants.REPRESENTATION_REF;

	}

	/**
	 * Looks at the annotations on the given <code>resource</code> to get the
	 * properties to include given the <code>representation</code>
	 * 
	 * TODO: make this a List<String> instead of a String[] after testing
	 * 
	 * @param representation
	 *            default/full/standard (cannot be null)
	 * @return list of strings to include
	 */
	private String[] getPropsToInclude(OpenmrsResource resource,
			String representation) {

		// TODO loop over @WebServiceProperty resource annotations and get
		// default list of props
		// TODO if rep==medium, get all default ones plus all medium ones
		// TODO if rep==full, loop over resource annotations and get list
		// of all props
		// TODO if rep==custom(*), split on comma and get all values. Then split
		// on period and only take the string before the first period

		String[] propsToInclude;

		// for now, just "hard code" which props so we can test
		if ("default".equals(representation))
			propsToInclude = resource.getDefaultRepresentation();
		else
			propsToInclude = representation.split(",");

		return propsToInclude;
	}

	/**
	 * Helper method to use the superclass of param class as well
	 * 
	 * @param c
	 * @param name
	 * @param param
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Method getMethod(Class c, String name, Class param) {

		Method m = null;
		try {
			m = c.getMethod(name, param);
		} catch (NoSuchMethodException ex) {
			// do nothing
		}

		if (m != null)
			return m;

		if (param.getSuperclass() != null) {
			return getMethod(c, name, param.getSuperclass());
		}

		return null;
		// throw new NoSuchMethodException("No method on class " + c +
		// " with name " + name + " with param " + param);
	}

	// copied from trunk but exchange boolean for OpenmrsObject return value
	protected static OpenmrsObject isOpenmrsObjectCollection(Object arg) {

		// kind of a hacky way to test for a list of openmrs objects, but java
		// strips out
		// the generic info for 1.4 compat, so we don't have access to that info
		// here
		try {
			Collection<Object> objects = (Collection<Object>) arg;
			if (!objects.isEmpty()) {
				return (OpenmrsObject) objects.iterator().next();
			} else {
				return null;
			}
		} catch (ClassCastException ex) {
			// do nothing
		}
		return null;
	}

	/**
	 * Auto generated method comment
	 * 
	 * @param openmrsObject
	 * @param properties
	 *            map from property name to property value/unique name/uuid
	 */
	public void setValues(OpenmrsObject openmrsObject,
			Map<String, Object> properties) {
		// TODO: implement this method

		// look up setter
		// check value
		// convert if necessary
		// set value

		// will reject setting of "retired" and "voided" and "dead". those
		// should be their own methods
	}

	/**
	 * Convenience method to get the uri property on all SimpleObject / Ref
	 * objects.
	 * 
	 * @param <OO>
	 *            the type of the OpenmrsObject
	 * @param resource
	 *            the OpenmrsResource defining this openmrs object
	 * @param openmrsObject
	 *            the OpenmrsObject itself (has the uuids, etc on it for
	 *            inserting into uri)
	 * @return a uri string specific to this object like
	 *         "http://myopenmrs.org:8080/openmrs/ws/rest/person/38423BD-3843-283492343"
	 */
	public <OO extends OpenmrsObject> String getURI(
			OpenmrsResource<OO> resource, OO openmrsObject) {
		String prefix = ""; // TODO: get webapp url and put it here
		prefix += WSConstants.URL_PREFIX;
		return prefix + resource.getURISuffix(openmrsObject);
	}
}

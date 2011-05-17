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
package org.openmrs.module.webservices.rest.web;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

/**
 * Convenient helper methods for the Rest Web Services module.
 * 
 */
public class RestUtil {

	private static Log log = LogFactory.getLog(RestUtil.class);

	/**
	 * Looks up the admin defined global property for the system limit
	 * 
	 * @return Integer limit
	 * @see #getLimit(WebRequest)
	 * @see RestConstants#MAX_RESULTS_GLOBAL_PROPERTY_NAME
	 */
	public static Integer getDefaultLimit() {
		// TODO check global property
		// WSConstants.MAX_RESULTS_GLOBAL_PROPERTY_NAME;
        
       String limit = Context.getAdministrationService().getGlobalProperty( RestConstants.MAX_RESULTS_GLOBAL_PROPERTY_NAME );
        if (StringUtils.isNotEmpty( limit ) )
        {
            try{
                return Integer.parseInt( limit );
            } catch (NumberFormatException nfex){
                return RestConstants.MAX_RESULTS_DEFAULT;
            }
        } else{
            return RestConstants.MAX_RESULTS_DEFAULT;
        }
	}

	/*
	 * TODO - move logic from here to a method to deal with custom representations
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
	 *
	public SimpleObject convert(OpenmrsResource resource,
			OpenmrsObject openmrsObject, String representation)
			throws Exception {

		if (representation == null)
			representation = RestConstants.REPRESENTATION_DEFAULT;

		if (resource == null)
			resource = HandlerUtil.getPreferredHandler(OpenmrsResource.class,
					openmrsObject.getClass());

		// the object to return. adds the default link/display/uuid properties
		SimpleObject simpleObject = new SimpleObject(resource, openmrsObject);

		// if they asked for a simple rep, we're done, just return that
		if (RestConstants.REPRESENTATION_REF.equals(representation))
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
					} else if (RestConstants.REPRESENTATION_FULL
							.equals(representation)
							|| RestConstants.REPRESENTATION_MEDIUM
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
	*/

	
	/* Used by code commented out above.  Ready for possible deletion.
	 * 
	 * TODO: look into whether this can use PropertyUtils instead
	 
	/**
	 * Helper method to use the superclass of param class as well
	 * 
	 * @param c
	 * @param name
	 * @param param
	 * @return
	 *
	public Method getMethod(Class<?> c, String name, Class<?> param) {

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
	*/

	/**
	 * Determines the request representation, if not provided, uses default.
	 * <br/>
	 * Determines number of results to limit to, if not provided, uses default set by admin.
	 * <br/>
	 * Determines how far into a list to start with given the startIndex param.
	 * <br/>
	 * 
	 * @param request the current http web request
	 * @return a {@link RequestContext} object filled with all the necessary values
	 * @see RestConstants#REQUEST_PROPERTY_FOR_LIMIT
	 * @see RestConstants#REQUEST_PROPERTY_FOR_REPRESENTATION
	 * @see RestConstants#REQUEST_PROPERTY_FOR_START_INDEX
	 */
	public static RequestContext getRequestContext(WebRequest request) {
	    RequestContext ret = new RequestContext();
	    
	    // get the "v" param for the representations
	    String temp = request.getParameter(RestConstants.REQUEST_PROPERTY_FOR_REPRESENTATION);
	    if (StringUtils.isEmpty(temp)) {
	    	ret.setRepresentation(Representation.DEFAULT);
	    } else if (temp.equals(RestConstants.REPRESENTATION_DEFAULT)) {
	    	throw new IllegalArgumentException("Do not specify ?v=default");
	    } else {
	    	ret.setRepresentation(Context.getService(RestService.class).getRepresentation(temp));
	    }
	    
	    Integer tempInt;
	    
	    // fetch the "limit" param
	    temp = request.getParameter(RestConstants.REQUEST_PROPERTY_FOR_LIMIT);
	    try {
		    tempInt = new Integer(temp);
		    if (tempInt != null) {
		    	ret.setLimit(tempInt);
		    }
	    }
	    catch (NumberFormatException e) {
	    	log.debug("unable to parse 'limit' parameter into a valid integer: " + temp);
	    }
	    
	    // fetch the startIndex param
	    temp = request.getParameter(RestConstants.REQUEST_PROPERTY_FOR_START_INDEX);
	    try {
		    tempInt = new Integer(temp);
		    if (tempInt != null) {
		    	ret.setStartIndex(tempInt);
		    }
	    }
	    catch (NumberFormatException e) {
	    	log.debug("unable to parse 'startIndex' parameter into a valid integer: " + temp);
	    }
	    
	    return ret;
    }

	/**
	 * Sets the HTTP status on the response according to the exception
	 * @param ex
	 * @param response
	 */
	public static void setResponseStatus(Throwable ex, HttpServletResponse response) {
		ResponseStatus ann = ex.getClass().getAnnotation(ResponseStatus.class);
		if (ann != null) {
			if (StringUtils.isNotBlank(ann.reason()))
				response.setStatus(ann.value().value(), ann.reason());
			else
				response.setStatus(ann.value().value());
		} else {
	    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
    }

	/**
	 * Sets the HTTP status on the response to no content, and returns an empty value, suitable for
	 * returning from a @ResponseBody annotated Spring controller method.
	 * @param response
	 * @return
	 */
	public static Object noContent(HttpServletResponse response) {
	    response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	    return "";
    }

	/**
	 * Sets the HTTP status for CREATED and (if 'created' has a uri) the Location header attribute 
	 * @param response
	 * @param created
	 * @return the object passed in
	 */
	public static Object created(HttpServletResponse response, Object created) {
		response.setStatus(HttpServletResponse.SC_CREATED);
		try {
			String uri = (String) PropertyUtils.getProperty(created, "uri");
			response.addHeader("Location", uri);
		} catch (Exception ex) { }
		return created;
    }
	
}

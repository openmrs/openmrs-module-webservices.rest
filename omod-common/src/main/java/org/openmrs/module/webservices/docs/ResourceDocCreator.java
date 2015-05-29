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
package org.openmrs.module.webservices.docs;

import org.apache.commons.lang.StringUtils;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.api.SearchQuery;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainResourceController;
import org.openmrs.module.webservices.rest.web.v1_0.controller.MainSubResourceController;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Creates documentation about web service resources.
 */
public class ResourceDocCreator {
	
	/**
	 * Creates a map of resource names and their documentation objects.
	 * 
	 * @param baseUrl the base or root for all the urls. e.g http://localhost:8080/openmrs
	 * @return a map of ResourceData objects keyed by their resource names.
	 * @throws IOException
	 */
	public static Map<String, ResourceDoc> createDocMap(String baseUrl) throws IllegalAccessException,
	        InstantiationException, IOException, ConversionException {
		
		Map<String, ResourceDoc> resouceDocMap = new HashMap<String, ResourceDoc>();
		
		List<DelegatingResourceHandler<?>> resourceHandlers = Context.getService(RestService.class).getResourceHandlers();
		
		//Context.getService(RestService.class).
		
		fillRepresentations(resourceHandlers, resouceDocMap);
		//fillOperations(resourceDocMap);
		fillUrls(baseUrl, resouceDocMap);
		
		return resouceDocMap;
	}
	
	/**
	 * Creates a list of resource documentation objects.
	 * 
	 * @return the documentation object list.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static List<ResourceDoc> create(String baseUrl) throws IllegalAccessException, InstantiationException,
	        IOException, ConversionException {
		
		List<ResourceDoc> docs = new ArrayList<ResourceDoc>();
		
		docs.addAll(createDocMap(baseUrl).values());
		
		//Remove resources for subtype handlers
		for (Iterator<ResourceDoc> it = docs.iterator(); it.hasNext();) {
			ResourceDoc resourceDoc = it.next();
			if (resourceDoc.isSubtypeHandler()) {
				it.remove();
			}
		}
		Collections.sort(docs);
		
		return docs;
	}
	
	public static List<SearchHandlerDoc> createSearchHandlerDoc(String baseUrl) throws IllegalAccessException,
	        InstantiationException, IOException, ConversionException {
		
		List<SearchHandler> searchHandlers = Context.getService(RestService.class).getAllSearchHandlers();
		
		List<SearchHandlerDoc> docs = fillSearchHandlers(searchHandlers, baseUrl);
		
		Collections.sort(docs);
		
		return docs;
	}
	
	/**
	 * Fills a map of resource names and their documentation objects with resource representations.
	 * 
	 * @param resourceHandlers resource classes.
	 * @param resourceDocMap a map of each resource name and its corresponding documentation object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void fillRepresentations(List<DelegatingResourceHandler<?>> resourceHandlers,
	        Map<String, ResourceDoc> resourceDocMap) throws IllegalAccessException, InstantiationException,
	        ConversionException {
		
		//We want to handle all resources before their subresources
		Collections.sort(resourceHandlers, new Comparator<DelegatingResourceHandler<?>>() {
			
			@Override
			public int compare(DelegatingResourceHandler<?> left, DelegatingResourceHandler<?> right) {
				return isSubclass(left).compareTo(isSubclass(right));
			}
			
			private Boolean isSubclass(DelegatingResourceHandler<?> resourceHandler) {
				return resourceHandler.getClass().getAnnotation(
				    org.openmrs.module.webservices.rest.web.annotation.SubResource.class) != null;
			}
		});
		
		//Go through all resource classes asking each for its default, ref and full representation.                                                                                                   InstantiationException {
		for (DelegatingResourceHandler<?> resourceHandler : resourceHandlers) {
			if (resourceHandler.getClass().getName()
			        .equals("org.openmrs.module.webservices.rest.web.HivDrugOrderSubclassHandler")
			        || resourceHandler.getClass().getName()
			                .equals("org.openmrs.module.webservices.rest.web.v1_0.test.GenericChildResource")) {
				continue; //Skip the test class
			}
			
			Object delegate = null;
			try {
				delegate = resourceHandler.newDelegate();
			}
			catch (ResourceDoesNotSupportOperationException ex) {
				continue;
			}
			if (delegate == null) {
				// TODO: handle resources that don't implement newDelegate(), e.g. ConceptSearchResource1_9, all subclasses of EvaluatedResource in the reportingrest module
				continue;
			}
			
			String resourceClassname = delegate.getClass().getSimpleName();
			if (resourceClassname.equals("UserAndPassword1_8")) {
				resourceClassname = "User"; //Work-around for UserAndPassword to be displayed as User
			} else if (resourceClassname.equals("CohortMember1_8")) {
				resourceClassname = "CohortMember";
			} else if (resourceClassname.equals("IncomingHl7Message1_8")) {
				resourceClassname = "HL7";
			}
			
			String subResourceForClass = null;
			ResourceDoc resourceDoc = new ResourceDoc(resourceClassname);
			resourceDoc.setResourceVersion(resourceHandler.getResourceVersion());
			org.openmrs.module.webservices.rest.web.annotation.Resource resourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) resourceHandler
			        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
			if (resourceAnnotation != null) {
				resourceDoc.setResourceName(resourceAnnotation.name());
				
				String[] supportedVersions = resourceAnnotation.supportedOpenmrsVersions();
				List<String> supportedVersionsList = new ArrayList<String>();
				
				for (String version : supportedVersions)
					supportedVersionsList.add(version);
				
				resourceDoc.setSupportedOpenMRSVersion(supportedVersionsList);
				
			} else {
				//this is a subResource, use the name of the collection
				org.openmrs.module.webservices.rest.web.annotation.SubResource subResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.SubResource) resourceHandler
				        .getClass().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.SubResource.class));
				if (subResourceAnnotation != null) {
					org.openmrs.module.webservices.rest.web.annotation.Resource parentResourceAnnotation = ((org.openmrs.module.webservices.rest.web.annotation.Resource) subResourceAnnotation
					        .parent().getAnnotation(org.openmrs.module.webservices.rest.web.annotation.Resource.class));
					
					resourceDoc.setResourceName(parentResourceAnnotation.name());
					resourceDoc.setSubResourceName(subResourceAnnotation.path());
					
					subResourceForClass = parentResourceAnnotation.supportedClass().getSimpleName();
				}
			}
			
			Object instance = resourceHandler;
			
			// subtype handlers are not resources themselves, but further specify resources (e.g. drugorder for order)
			if (resourceHandler instanceof DelegatingSubclassHandler) {
				Class<?> resourceClass = ((DelegatingSubclassHandler<?, ?>) resourceHandler).getSuperclass();
				instance = Context.getService(RestService.class).getResourceBySupportedClass(resourceClass);
				//Add as a subtype handler
				ResourceDoc actualResourceDoc = resourceDocMap.get(resourceClass.getSimpleName());
				if (actualResourceDoc == null) {
					actualResourceDoc = new ResourceDoc(resourceClass.getSimpleName());
					resourceDocMap.put(actualResourceDoc.getName(), actualResourceDoc);
				}
				actualResourceDoc.addSubtypeHandler(resourceDoc);
			}
			
			if (resourceDoc.isSubResource()) {
				// Add as a subresource to an existing resource
				ResourceDoc parentResourceDoc = resourceDocMap.get(subResourceForClass);
				parentResourceDoc.addSubResource(resourceDoc);
			} else {
				// Add as a resource
				
				ResourceDoc previous = resourceDocMap.put(resourceDoc.getName(), resourceDoc);
				
				// in case we've put in a placeholder (since a subtype handler was processed before its resource)
				// we need to preserve its children
				if (previous != null) {
					for (ResourceDoc subResource : previous.getSubResources()) {
						resourceDoc.addSubResource(subResource);
					}
					for (ResourceDoc subtypeHandler : previous.getSubtypeHandlers()) {
						resourceDoc.addSubtypeHandler(subtypeHandler);
					}
				}
			}
			
			//GET representations
			Representation[] representations = new Representation[] { Representation.REF, Representation.DEFAULT,
			        Representation.FULL };
			
			for (Representation representation : representations) {
				if (instance instanceof Converter) {
					try {
						@SuppressWarnings("unchecked")
						Converter<Object> converter = (Converter<Object>) instance;
						SimpleObject simpleObject = converter.asRepresentation(delegate, representation);
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), simpleObject.keySet()));
					}
					catch (Exception e) {
						resourceDoc.addRepresentation(new ResourceRepresentation(
						        "GET " + representation.getRepresentation(), Arrays.asList("Not supported")));
					}
				} else {
					resourceDoc.addRepresentation(new ResourceRepresentation("GET " + representation.getRepresentation(),
					        Arrays.asList("Not supported")));
				}
			}
			
			//POST create representations
			try {
				DelegatingResourceDescription description = resourceHandler.getCreatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST create", Arrays.asList("Not supported")));
			}
			
			//POST update representations
			try {
				DelegatingResourceDescription description = resourceHandler.getUpdatableProperties();
				List<String> properties = getPOSTProperties(description);
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", properties));
			}
			catch (ResourceDoesNotSupportOperationException e) {
				resourceDoc.addRepresentation(new ResourceRepresentation("POST update", Arrays.asList("Not supported")));
			}
			
		}
	}
	
	/**
	 * Returns a list of POST properties of the given description.
	 * 
	 * @param description
	 */
	private static List<String> getPOSTProperties(DelegatingResourceDescription description) {
		List<String> properties = new ArrayList<String>();
		for (Entry<String, Property> property : description.getProperties().entrySet()) {
			if (property.getValue().isRequired()) {
				properties.add("*" + property.getKey() + "*");
			} else {
				properties.add(property.getKey());
			}
		}
		return properties;
	}
	
	/**
	 * Fills a map of resource names and their documentation objects with resource operations.
	 * 
	 * @param resouceDocMap a map of each resource name and its corresponding documentation object.
	 */
	private static void fillOperations(Map<String, ResourceDoc> resouceDocMap) throws IOException {
		
		File directory = new File(new File("").getAbsolutePath()
		        + "/src/main/java/org/openmrs/module/webservices/rest/web/resource");
		
		//Look for all resource files in the resource folder.
		String[] files = directory.list();
		if (files == null)
			return;
		
		for (int i = 0; i < files.length; i++) {
			String file = files[i];
			
			//We are only interested in ......Resource.java files
			if (file.endsWith("Resource.java")) {
				
				//Resource name is class name without the .java extension
				String name = file.subSequence(0, file.length() - "Resource.java".length()).toString();
				ResourceDoc resourceDoc = resouceDocMap.get(name);
				
				//Get the complete path and name of the java source file.
				String fullPathName = directory.getAbsolutePath() + File.separator + file;
				
				String source = OpenmrsUtil.getFileAsString(new File(fullPathName));
				
				//Parse the file's JavaDoc annotations to get the supported web service operations.
				resourceDoc.setOperations(JavadocParser.parse(source));
			}
		}
	}
	
	/**
	 * Fills a map of resource names and their documentation objects with resource urls.
	 * 
	 * @param baseUrl the base or root for all the urls. e.g http://localhost:8080/openmrs
	 * @param resouceDocMap a map of each resource name and its corresponding documentation object.
	 */
	private static void fillUrls(String baseUrl, Map<String, ResourceDoc> resouceDocMap) throws IOException {
		List<ResourceOperation> resourceOperations = null;
		List<ResourceOperation> subResourceOperations = null;
		String resourceUrl = baseUrl + "/rest";
		
		for (ResourceDoc doc : resouceDocMap.values()) {
			//skip subclass handlers e.g DrugOrderSubclassHandler
			if (doc.isSubtypeHandler()) {
				continue;
			}
			
			if (doc.getSubResourceName() == null) {
				if (resourceOperations == null)
					resourceOperations = getResourceOperations(resourceUrl, MainResourceController.class, true);
				
				for (ResourceOperation ro : resourceOperations) {
					//Add the actual urls after replacing the {resource} string with the resource name
					doc.addOperation(new ResourceOperation(StringUtils.replace(ro.getName(), "{resource}",
					    doc.getResourceName()), ro.getDescription()));
				}
				
				//Set the root url.
				doc.setUrl(resourceUrl + "/" + doc.getResourceName());
				setUrlInSubResources(doc.getUrl(), doc);
			} else {
				//This is a sub resource that has a parent
				if (subResourceOperations == null)
					subResourceOperations = getResourceOperations(resourceUrl, MainSubResourceController.class, false);
				
				for (ResourceOperation sro : subResourceOperations) {
					//generate the actual url to match the parent resource and subresource
					String operationUrl = StringUtils.replaceEach(sro.getName(), new String[] { "{resource}",
					        "{subResource}" }, new String[] { doc.getResourceName(), doc.getSubResourceName() });
					doc.addOperation(new ResourceOperation(operationUrl, sro.getDescription()));
				}
				
				doc.setUrl(resourceUrl + "/" + doc.getResourceName() + "/{parentUuid}/" + doc.getSubResourceName());
				setUrlInSubResources(resourceUrl, doc);
			}
		}
	}
	
	/**
	 * Sets URL in subResources.
	 * 
	 * @param parentUrl
	 * @param doc
	 */
	private static void setUrlInSubResources(String parentUrl, ResourceDoc doc) {
		for (ResourceDoc subResource : doc.getSubResources()) {
			subResource.setUrl(parentUrl + "/{uuid}/" + subResource.getSubResourceName());
			setUrlInSubResources(subResource.getUrl(), subResource);
		}
	}
	
	/**
	 * Gets a request method description specified for a given web service request handling method.
	 * 
	 * @param requestMapping the request mapping annotation.
	 * @param operation the HTTP operation method.
	 * @param method the method.
	 * @param supportsSearching specified if the controller the method belongs supports searching
	 * @return the method description string.
	 */
	private static String getMethodDescription(RequestMapping requestMapping, String operation, Method method,
	        boolean supportsSearching) {
		
		//If the documentation annotation exists, then no need for auto generating the description.
		WSDoc docAnnotation = (WSDoc) method.getAnnotation(WSDoc.class);
		if (docAnnotation != null)
			return docAnnotation.value();
		
		String value = null;
		if (requestMapping.value().length > 0 && StringUtils.isNotEmpty(requestMapping.value()[0]))
			value = requestMapping.value()[0];
		
		if (operation.equals("GET")) {
			if (value != null && value.contains("uuid"))
				return "Fetch by unique uuid";
			if (!supportsSearching)
				return "Fetch all non-retired";
			return "Fetch all non-retired that match any specified parameters otherwise fetch all non-retired";
			
		} else if (operation.equals("POST")) {
			if (value != null && value.contains("uuid"))
				return "Edit with given uuid, only modifying properties in request";
			else
				return "Create with properties in request";
			
		} else if (operation.equals("DELETE")) {
			if (!requestMapping.params()[0].startsWith("!"))
				return "Delete this object from the database";
			else
				return "Retire/Void this object";
		}
		
		return null;
	}
	
	/**
	 * Generates {@link ResourceOperation}s corresponding to the supported http methods
	 * 
	 * @param url
	 * @param clazz
	 * @param supportsSearching specified if the controller the method belongs supports searching
	 * @return
	 */
	private static List<ResourceOperation> getResourceOperations(String url, Class<?> clazz, boolean supportsSearching) {
		List<ResourceOperation> resourceOperations = new ArrayList<ResourceOperation>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			RequestMapping antn = (RequestMapping) method.getAnnotation(RequestMapping.class);
			if (antn == null)
				continue;
			
			String requestMethod = antn.method()[0].name();
			
			if (requestMethod.equals("TRACE")) {
				//Skip TRACE, which is used to disable a method in HL7MessageController.
				continue;
			}
			
			String operationUrl = requestMethod + " " + url;
			
			if (antn.value().length > 0)
				operationUrl += antn.value()[0];
			
			String paramString = null;
			for (String param : antn.params()) {
				if (paramString == null)
					paramString = param;
				else
					paramString += ("&" + param);
			}
			
			if (paramString != null)
				operationUrl += "?" + paramString;
			
			resourceOperations.add(new ResourceOperation(operationUrl, getMethodDescription(antn, requestMethod, method,
			    supportsSearching)));
		}
		
		Collections.sort(resourceOperations);
		
		return resourceOperations;
	}
	
	private static List<SearchHandlerDoc> fillSearchHandlers(List<SearchHandler> searchHandlers, String url) {
		
		List<SearchHandlerDoc> searchHandlerDocList = new ArrayList<SearchHandlerDoc>();
		
		for (SearchHandler searchHandler : searchHandlers) {
			
			SearchHandlerDoc searchHandlerDoc = new SearchHandlerDoc(searchHandler, url);
			searchHandlerDocList.add(searchHandlerDoc);
		}
		
		return searchHandlerDocList;
	}
}

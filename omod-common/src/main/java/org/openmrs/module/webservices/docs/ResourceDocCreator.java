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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.OpenmrsClassScanner;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.Resource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription.Property;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.v1_0.controller.BaseRestController;
import org.openmrs.util.HandlerUtil;
import org.openmrs.util.OpenmrsUtil;
import org.springframework.web.bind.annotation.RequestMapping;

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
		
		List<Class<? extends DelegatingResourceHandler>> classes = OpenmrsClassScanner.getInstance().getClasses(
		    DelegatingResourceHandler.class, true);
		
		fillRepresentations(classes.toArray(new Class<?>[0]), resouceDocMap);
		//fillOperations(resouceDocMap);
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
		
		//Remove subresources and resources without controllers
		for (Iterator<ResourceDoc> it = docs.iterator(); it.hasNext();) {
			ResourceDoc resourceDoc = it.next();
			if (resourceDoc.getUrl() == null)
				it.remove();
			else if (resourceDoc.getSuperResource() != null)
				it.remove();
		}
		
		Collections.sort(docs);
		
		return docs;
	}
	
	/**
	 * Fills a map of resource names and their documentation objects with resource representations.
	 * 
	 * @param classes resource classes.
	 * @param resouceDocMap a map of each resource name and its corresponding documentation object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void fillRepresentations(Class<?>[] classes, Map<String, ResourceDoc> resouceDocMap)
	        throws IllegalAccessException, InstantiationException, ConversionException {
		
		//Go through all resource classes asking each for its default, ref and full representation.                                                                                                   InstantiationException {
		for (Class<?> cls : classes) {
			if (!DelegatingResourceHandler.class.isAssignableFrom(cls)) {
				continue;
			}
			
			if (cls.getName().equals("org.openmrs.module.webservices.rest.web.HivDrugOrderSubclassHandler")) {
				continue; //Skip the test class
			}
			
			Object instance = null;
			
			try {
				instance = cls.newInstance();
			}
			catch (Exception ex) {
				//May be an abstract class which is not instantiable.
				continue;
			}
			
			DelegatingResourceHandler<?> resourceHandler = (DelegatingResourceHandler<?>) instance;
			
			Object delegate = resourceHandler.newDelegate();
			
			String resourceName = delegate.getClass().getSimpleName();
			if (resourceName.equals("UserAndPassword")) {
				resourceName = "User"; //Work-around for UserAndPassword to be displayed as User
			}
			
			ResourceDoc resourceDoc = new ResourceDoc(resourceName);
			
			if (instance instanceof DelegatingSubclassHandler) {
				Class<?> superclass = ((DelegatingSubclassHandler<?, ?>) instance).getSuperclass();
				instance = HandlerUtil.getPreferredHandler(Resource.class, superclass);
				//Add as a subresource
				ResourceDoc superclassResourceDoc = resouceDocMap.get(superclass.getSimpleName());
				if (superclassResourceDoc == null) {
					superclassResourceDoc = new ResourceDoc(superclass.getSimpleName());
					resouceDocMap.put(superclassResourceDoc.getName(), superclassResourceDoc);
				}
				superclassResourceDoc.addSubResource(resourceDoc);
			}
			//Add as a resource
			ResourceDoc previous = resouceDocMap.put(resourceDoc.getName(), resourceDoc);
			if (previous != null) {
				for (ResourceDoc subResource : previous.getSubResources()) {
					resourceDoc.addSubResource(subResource);
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
	 * @param resourceDoc
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
		
		List<Class<? extends BaseRestController>> controllers = OpenmrsClassScanner.getInstance().getClasses(
		    BaseRestController.class, true);
		
		for (Class<? extends BaseRestController> cls : controllers) {
			if (BaseRestController.class.equals(cls))
				continue;
			
			RequestMapping annotation = (RequestMapping) cls.getAnnotation(RequestMapping.class);
			if (annotation == null)
				continue;
			
			String url = baseUrl + annotation.value()[0];
			
			ResourceDoc doc = resouceDocMap.get(cls.getSimpleName().replace("Controller", ""));
			if (doc == null) {
				continue;
			}
			
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				if (method.getName().equals("getResourceCatalog")) {
					continue;
				}
				
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
				
				doc.addOperation(new ResourceOperation(operationUrl, getMethodDescription(antn, antn.method()[0].name(),
				    method)));
			}
			
			//Set the root url.
			doc.setUrl(url);
			setUrlInSubResources(url, doc);
			
			//Sort the operations
			ResourceOperation[] operations = doc.getOperations().toArray(new ResourceOperation[0]);
			Arrays.sort(operations);
			doc.setOperations(Arrays.asList(operations));
		}
	}
	
	/**
	 * Sets URL in subResources.
	 * 
	 * @param url
	 * @param doc
	 */
	private static void setUrlInSubResources(String url, ResourceDoc doc) {
		for (ResourceDoc subResource : doc.getSubResources()) {
			subResource.setUrl(url);
			setUrlInSubResources(url, subResource);
		}
	}
	
	/**
	 * Gets a request method description specified for a given web service request handling method.
	 * 
	 * @param requestMapping the request mapping annotation.
	 * @param operation the HTTP operation method.
	 * @param method the method.
	 * @return the method description string.
	 */
	private static String getMethodDescription(RequestMapping requestMapping, String operation, Method method) {
		
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
			else if (value == null && requestMapping.params().length == 0)
				return "Fetch all non-retired";
			else if (value == null && requestMapping.params().length > 0)
				return "Fetch all non-retired that match this parameter";
			
		} else if (operation.equals("POST")) {
			if (value != null && value.contains("uuid"))
				return "Edit with given uuid, only modifying properties in request";
			else
				return "Create with properties in request";
			
		} else if (operation.equals("DELETE")) {
			if (requestMapping.params().length > 0)
				return "Delete this object from the database";
			else
				return "Retire this object";
		}
		
		return null;
	}
}

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.annotation.WSDoc;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
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
	                                                                   InstantiationException, IOException,
	                                                                   ConversionException {
		
		Map<String, ResourceDoc> resouceDocMap = new HashMap<String, ResourceDoc>();
		
		List<Class<?>> classes = RestUtil.getClassesForPackage("org.openmrs.module.webservices.rest.web.resource",
		    "Resource.class");
		
		fillRepresentations(classes, resouceDocMap);
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
		
		ResourceDoc[] docArray = createDocMap(baseUrl).values().toArray(new ResourceDoc[0]);
		Arrays.sort(docArray);
		return Arrays.asList(docArray);
	}
	
	/**
	 * Fills a map of resource names and their documentation objects with resource representations.
	 * 
	 * @param classes resource classes.
	 * @param resouceDocMap a map of each resource name and its corresponding documentation object.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	private static void fillRepresentations(List<Class<?>> classes, Map<String, ResourceDoc> resouceDocMap)
	                                                                                                       throws IllegalAccessException,
	                                                                                                       InstantiationException,
	                                                                                                       ConversionException {
		
		//Go through all resource classes asking each for its default, ref and full representation.                                                                                                   InstantiationException {
		for (Class<?> cls : classes) {
			Object instance = null;
			
			try {
				instance = cls.newInstance();
			}
			catch (Exception ex) {
				//May be an abstract class which is not instantiable.
				continue;
			}
			
			try {
				if (instance instanceof BaseDelegatingResource) {
					
					ResourceDoc resourceDoc = new ResourceDoc(cls.getSimpleName().replace("Resource", ""));
					resouceDocMap.put(resourceDoc.getName(), resourceDoc);
					
					Method method = instance.getClass().getDeclaredMethod("newDelegate", null);
					method.setAccessible(true);
					Object delegate = method.invoke(instance, null);
					
					//Get the ref representation of this resource.
					Object rep = ((BaseDelegatingResource<Object>) instance).asRepresentation(delegate, Representation.REF);
					Set<String> properties = ((SimpleObject) rep).keySet();
					properties.remove("links");
					resourceDoc.addRepresentation(new ResourceRepresentation("ref", properties));
					
					//Get the default representation of this resource..
					rep = ((BaseDelegatingResource<Object>) instance).asRepresentation(delegate, Representation.DEFAULT);
					properties = ((SimpleObject) rep).keySet();
					properties.remove("links");
					resourceDoc.addRepresentation(new ResourceRepresentation("default", properties));
					
					//Get the full representation of this resource.
					rep = ((BaseDelegatingResource<Object>) instance).asRepresentation(delegate, Representation.FULL);
					properties = ((SimpleObject) rep).keySet();
					properties.remove("links");
					resourceDoc.addRepresentation(new ResourceRepresentation("full", properties));
					
					/*BaseDelegatingResource<?> resoure = (BaseDelegatingResource<?>) instance;
					
					//Get the default representation of this resource..
					DelegatingResourceDescription resoureDescription = resoure
					        .getRepresentationDescription(Representation.DEFAULT);
					if (resoureDescription != null) {
						resourceDoc.addRepresentation(new ResourceRepresentation("default", resoureDescription
						        .getProperties().keySet()));
					}
					
					//Get the full representation of this resource.
					resoureDescription = resoure.getRepresentationDescription(Representation.FULL);
					if (resoureDescription != null) {
						resourceDoc.addRepresentation(new ResourceRepresentation("full", resoureDescription.getProperties()
						        .keySet()));
					}
					
					//Get the ref representation of this resource.
					resoureDescription = resoure.getRepresentationDescription(Representation.REF);
					if (resoureDescription != null) {
						resourceDoc.addRepresentation(new ResourceRepresentation("ref", resoureDescription.getProperties()
						        .keySet()));
					}*/
				}
			}
			catch (NoSuchMethodException ex) {
				ex.printStackTrace();
			}
			catch (InvocationTargetException ex) {
				ex.printStackTrace();
			}
		}
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
		List<Class<?>> controllers = RestUtil.getClassesForPackage("org.openmrs.module.webservices.rest.web.controller",
		    "Controller.class");
		
		for (Class<?> cls : controllers) {
			RequestMapping annotation = (RequestMapping) cls.getAnnotation(RequestMapping.class);
			if (annotation == null)
				continue;
			
			if (cls.getSimpleName().equals("BaseRestController") || cls.getSimpleName().equals("SettingsFormController")
			        || cls.getSimpleName().equals("SessionController") || cls.getSimpleName().equals("HelpController")) {
				continue;
			}
			
			String url = baseUrl + annotation.value()[0];
			ResourceDoc doc = resouceDocMap.get(cls.getSimpleName().replace("Controller", ""));
			
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				RequestMapping antn = (RequestMapping) method.getAnnotation(RequestMapping.class);
				if (antn == null)
					continue;
				
				String operationUrl = antn.method()[0].name() + " " + url;
				
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
				
				doc.addOperation(new ResourceOperation(operationUrl, getMethodDescription(antn, antn.method()[0].name(), method)));
			}
			
			//Set the root url.
			doc.setUrl(url);
			
			//Sort the operations
			ResourceOperation[] operations = doc.getOperations().toArray(new ResourceOperation[0]);
			Arrays.sort(operations);
			doc.setOperations(Arrays.asList(operations));
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
		if (requestMapping.value().length > 0)
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
			if(requestMapping.params().length > 0)
				return "Delete this object from the database";
			else
				return "Retire this object";
		}
		
		return null;
	}
}

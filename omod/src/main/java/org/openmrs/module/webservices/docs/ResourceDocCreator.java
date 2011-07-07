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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.webservices.rest.web.RestUtil;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
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
	        InstantiationException, IOException, ConversionException {
		
		Map<String, ResourceDoc> resouceDocMap = new HashMap<String, ResourceDoc>();
		
		List<Class<?>> classes = RestUtil.getClassesForPackage("org.openmrs.module.webservices.rest.web.resource",
		    "Resource.class");
		
		fillRepresentations(classes, resouceDocMap);
		fillOperations(resouceDocMap);
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
		
		List<ResourceDoc> docList = new ArrayList<ResourceDoc>();
		docList.addAll(createDocMap(baseUrl).values());
		return docList;
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
	        throws IllegalAccessException, InstantiationException, ConversionException {
		
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
			
			if (instance instanceof BaseDelegatingResource) {
				
				ResourceDoc resourceDoc = new ResourceDoc(cls.getSimpleName().replace("Resource", ""));
				resouceDocMap.put(resourceDoc.getName(), resourceDoc);
				
				//Object obj = ((BaseDelegatingResource<Object>) instance).asRepresentation(((DelegatingCrudResource)instance).newDelegate(), Representation.DEFAULT);
				
				//Get the default representation of this resource.
				BaseDelegatingResource<?> resoure = (BaseDelegatingResource<?>) instance;
				DelegatingResourceDescription resoureDescription = resoure
				        .getRepresentationDescription(Representation.DEFAULT);
				
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("ref", resoureDescription.getProperties()
					        .keySet()));
				}
				
				//Get the ref representation of this resource.
				resoureDescription = resoure.getRepresentationDescription(Representation.REF);
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("default", resoureDescription.getProperties()
					        .keySet()));
				}
				
				//Get the fill representation of this resource.
				resoureDescription = resoure.getRepresentationDescription(Representation.FULL);
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("full", resoureDescription.getProperties()
					        .keySet()));
				}
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
			
			resouceDocMap.get(cls.getSimpleName().replace("Controller", "")).setUrl(baseUrl + annotation.value()[0]);
		}
	}
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;

/**
 * Creates documentation about web service resources.
 */
public class ResourceDocCreator {
	
	/**
	 * Creates a list of resource documentation objects.
	 * 
	 * @return the documentation object list.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static List<ResourceDoc> create() throws IllegalAccessException, InstantiationException {
		
		Map<String, ResourceDoc> resouceDocMap = new HashMap<String, ResourceDoc>();
		
		List<Class<?>> classes = ResourceLocator.getResourceClasses();
		
		fillRepresentations(classes, resouceDocMap);
		fillOperations(resouceDocMap);
		
		List<ResourceDoc> docList = new ArrayList<ResourceDoc>();
		docList.addAll(resouceDocMap.values());
		
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
	                                                                                                       throws IllegalAccessException,
	                                                                                                       InstantiationException {
		
		//Go through all resource classes asking each for its default, ref and full representation.                                                                                                   InstantiationException {
		for (Class<?> cls : classes) {
			Object instance = cls.newInstance();
			if (instance instanceof BaseDelegatingResource) {
				
				ResourceDoc resourceDoc = new ResourceDoc(cls.getName().substring(cls.getName().lastIndexOf('.') + 1));
				resouceDocMap.put(resourceDoc.getName(), resourceDoc);
				
				//Get the default representation of this resource.
				BaseDelegatingResource<?> resoure = (BaseDelegatingResource<?>) instance;
				DelegatingResourceDescription resoureDescription = resoure
				        .getRepresentationDescription(Representation.DEFAULT);
				
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("DEFAULT", resoureDescription.getProperties()
					        .keySet()));
				}
				
				//Get the ref representation of this resource.
				resoureDescription = resoure.getRepresentationDescription(Representation.REF);
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("REF", resoureDescription.getProperties()
					        .keySet()));
				}
				
				//Get the fill representation of this resource.
				resoureDescription = resoure.getRepresentationDescription(Representation.FULL);
				if (resoureDescription != null) {
					resourceDoc.addRepresentation(new ResourceRepresentation("FULL", resoureDescription.getProperties()
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
	private static void fillOperations(Map<String, ResourceDoc> resouceDocMap) {
		
		File directory = new File(new File("").getAbsolutePath()
		        + "/src/main/java/org/openmrs/module/webservices/rest/web/resource");
		
		//Look for all resource files in the resource folder.
		String[] files = directory.list();
		for (int i = 0; i < files.length; i++) {
			String file = files[i];
			
			//We are only interested in ......Resource.java files
			if (file.endsWith("Resource.java")) {
				
				//Resource name is class name without the .java extension
				String name = file.subSequence(0, file.length() - ".java".length()).toString();
				ResourceDoc resourceDoc = resouceDocMap.get(name);
				
				//Get the complete path and name of the java source file.
				String fullPathName = directory.getAbsolutePath() + File.separator + file;
				
				String source = FileUtil.getFileAsString(new File(fullPathName));
				
				//Parse the file's JavaDoc annotations to get the supported web service operations.
				resourceDoc.setOperations(JavadocParser.parse(source));
			}
		}
	}
}

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.util.OpenmrsClassLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;

/**
 * Reflection utilities to search the classpath for classes
 */
public class OpenmrsClassScanner {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final OpenmrsClassScanner instance = new OpenmrsClassScanner();
	
	private final MetadataReaderFactory metadataReaderFactory;
	
	private final ResourcePatternResolver resourceResolver;
	
	OpenmrsClassScanner() {
		
		this.metadataReaderFactory = new SimpleMetadataReaderFactory(OpenmrsClassLoader.getInstance());
		
		this.resourceResolver = new PathMatchingResourcePatternResolver(OpenmrsClassLoader.getInstance());
		
	}
	
	/**
	 * @return the instance
	 */
	
	public static OpenmrsClassScanner getInstance() {
		
		return instance;
		
	}
	
	/**
	 * Searches for classes extending or implementing the given type.
	 * 
	 * @param <T>
	 * @param type
	 * @param concrete true if only concrete classes should be returned
	 * @return the list of found classes
	 * @throws IOException
	 */
	
	public <T> List<Class<? extends T>> getClasses(Class<? extends T> type, boolean concrete) throws IOException {
		
		List<Class<? extends T>> types = new ArrayList<Class<? extends T>>();
		
		String pattern = "classpath*:org/openmrs/**/*.class";
		
		Resource[] resources = resourceResolver.getResources(pattern);
		
		TypeFilter typeFilter = new AssignableTypeFilter(type);
		
		for (Resource resource : resources) {
			
			try {
				
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
				
				if (typeFilter.match(metadataReader, metadataReaderFactory)) {
					
					if (concrete == metadataReader.getClassMetadata().isConcrete()) {
						
						String classname = metadataReader.getClassMetadata().getClassName();
						
						try {
							
							@SuppressWarnings("unchecked")
							Class<? extends T> metadata = (Class<? extends T>) OpenmrsClassLoader.getInstance().loadClass(
							
							classname);
							
							types.add(metadata);
							
						}
						
						catch (ClassNotFoundException e) {
							
							throw new IOException("Class cannot be loaded: " + classname, e);
							
						}
						
					}
					
				}
				
			}
			
			catch (IOException e) {
				
				log.debug("Resource cannot be loaded: " + resource);
				
			}
			
		}
		
		return types;
		
	}
}

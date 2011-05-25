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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Searches for web service resource classes.
 */
public class ResourceLocator {
	
	/**
	 * Gets a list of resource classes.
	 * 
	 * @return the list of classes.
	 */
	public static ArrayList<Class<?>> getResourceClasses() {
		return getClassesForPackage("org.openmrs.module.webservices.rest.web.resource");
	}
	
	/**
	 * Gets a list of classes in a given package.
	 * 
	 * @param pkgname the package name.
	 * @return the list of classes.
	 */
	private static ArrayList<Class<?>> getClassesForPackage(String pkgname) {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		
		//Get a File object for the package
		File directory = null;
		String relPath = pkgname.replace('.', '/');
		URL resource = ClassLoader.getSystemClassLoader().getResource(relPath);
		if (resource == null) {
			throw new RuntimeException("No resource for " + relPath);
		}
		
		try {
			directory = new File(resource.toURI());
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(pkgname + " (" + resource
			        + ") does not appear to be a valid URL / URI.  Strange, since we got it from the system...", e);
		}
		
		//If folder exists, look for all resource class files in it.
		if (directory.exists()) {
			
			//Get the list of the files contained in the package
			String[] files = directory.list();
			
			for (int i = 0; i < files.length; i++) {
				
				//We are only interested in Resource.class files
				if (files[i].endsWith("Resource.class")) {
					
					//Remove the .class extension
					String className = pkgname + '.' + files[i].substring(0, files[i].length() - 6);
					
					try {
						classes.add(Class.forName(className));
					}
					catch (ClassNotFoundException e) {
						throw new RuntimeException("ClassNotFoundException loading " + className);
					}
				}
			}
		} else {
			
			//Directory does not exist, look in jar file.
			try {
				String fullPath = resource.getFile();
				String jarPath = fullPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
				JarFile jarFile = new JarFile(jarPath);
				
				Enumeration<JarEntry> entries = jarFile.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
					
					String entryName = entry.getName();
					if (entryName.startsWith(relPath) && entryName.length() > (relPath.length() + "/".length())) {
						String className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
						
						try {
							classes.add(Class.forName(className));
						}
						catch (ClassNotFoundException e) {
							throw new RuntimeException("ClassNotFoundException loading " + className);
						}
					}
				}
			}
			catch (IOException e) {
				throw new RuntimeException(pkgname + " (" + directory + ") does not appear to be a valid package", e);
			}
		}
		
		return classes;
	}
}

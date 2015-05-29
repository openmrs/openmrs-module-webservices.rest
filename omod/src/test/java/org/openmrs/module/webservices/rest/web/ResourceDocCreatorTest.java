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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceDocCreator;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

import java.util.List;

/**
 * Unit test for generating as much documentation as can be got from the source code.
 */
@Ignore
public class ResourceDocCreatorTest extends BaseModuleWebContextSensitiveTest {
	
	@Ignore("This won't work unless you increase the openmrs-core version to 1.10.0")
	@Test
	public void testCreatingCatalog() throws Exception {
		List<ResourceDoc> resourceDocs = ResourceDocCreator.create("/ws");
		String json = new ObjectMapper().writeValueAsString(resourceDocs);
		System.out.println(json);
	}
	
	@Test
	@Ignore
	public void createDocumentation() throws Exception {
		
		List<ResourceDoc> docs = ResourceDocCreator.create("/ws");
		
		String header = "This page is generated automatically and must not be edited manually. It describes the urls/resources published by the [docs:Webservices.rest Module].";
		header += System.getProperty("line.separator");
		header += System.getProperty("line.separator");
		header += "For more information on conventions, authentication, and setup, see the main [docs:REST Web Services API] page.";
		header += System.getProperty("line.separator");
		header += System.getProperty("line.separator");
		header += "{toc:maxLevel=1}";
		header += System.getProperty("line.separator");
		header += "{note}";
		header += System.getProperty("line.separator");
		header += System.getProperty("line.separator");
		header += "When a parameter has a bang \"!\" in front of it, it means that url will only be called if that parameter does NOT exist.\n\n";
		header += "Properties listed in *bold* must not be null.";
		header += System.getProperty("line.separator");
		header += System.getProperty("line.separator");
		header += "{note}";
		header += System.getProperty("line.separator");
		
		System.out.println(header);
		
		for (ResourceDoc doc : docs) {
			
			String text = doc.toString();
			
			//Wiki syntax will think these are macros and complain like: Unknown macro: {......}
			text = text.replace("{", "\\{");
			text = text.replace("}", "\\}");
			
			System.out.println(text);
		}
	}
	
	@Test
	public void fakeTest() {
		
	}
}

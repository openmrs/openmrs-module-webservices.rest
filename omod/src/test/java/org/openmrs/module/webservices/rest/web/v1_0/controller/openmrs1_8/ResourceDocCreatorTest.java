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
package org.openmrs.module.webservices.rest.web.v1_0.controller.openmrs1_8;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.openmrs.module.webservices.docs.ResourceDoc;
import org.openmrs.module.webservices.docs.ResourceDocCreator;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;

/**
 * Unit test for generating as much documentation as can be got from the source code.
 */
public class ResourceDocCreatorTest extends BaseModuleWebContextSensitiveTest {
	
	@Test
	@Ignore
	public void createDocumentation() throws Exception {
		
		List<ResourceDoc> docs = ResourceDocCreator.create("/ws");
		
		String header = "This page describes the urls/resources published by the [docs:Webservices.rest Module].";
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
			
			//Skip resources without controllers. Controllers provide the urls.
			if (doc.getUrl() == null)
				continue;
			
			String text = doc.toString();
			
			//Wiki syntax will think these are macros and complain like: Unknown macro: {......}
			text = text.replace("{", "\\{");
			text = text.replace("}", "\\}");
			
			System.out.println(text);
		}
	}
}

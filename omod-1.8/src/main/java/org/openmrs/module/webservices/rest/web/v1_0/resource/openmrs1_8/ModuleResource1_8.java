/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.apache.commons.io.FileUtils;
import org.openmrs.module.Module;
import org.openmrs.module.webservices.helper.ModuleFactoryWrapper;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Uploadable;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingReadableResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.web.WebUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + "/module", supportedClass = Module.class, supportedOpenmrsVersions = { "1.8.*",
        "1.9.*", "1.10.*", "1.11.*", "1.12.*", "2.0.*", "2.1.*" })
public class ModuleResource1_8 extends BaseDelegatingReadableResource<Module> implements Uploadable {
	
	private ModuleFactoryWrapper moduleFactoryWrapper = new ModuleFactoryWrapper();
	
	private String moduleActionLink = ModuleActionResource1_8.class.getAnnotation(Resource.class).name();
	
	public void setModuleFactoryWrapper(ModuleFactoryWrapper moduleFactoryWrapper) {
		this.moduleFactoryWrapper = moduleFactoryWrapper;
	}
	
	@Override
	public Module getByUniqueId(String uniqueId) {
		moduleFactoryWrapper.checkPrivilege();
		return moduleFactoryWrapper.getModuleById(uniqueId);
	}
	
	@Override
	public Module newDelegate() {
		return null;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("packageName");
			description.addProperty("author");
			description.addProperty("version");
			description.addProperty("started");
			description.addProperty("startupErrorMessage");
			description.addProperty("requireOpenmrsVersion");
			description.addProperty("awareOfModules");
			description.addProperty("requiredModules");
			description.addLink("ref", ".?v=" + RestConstants.REPRESENTATION_REF);
			description.addLink("action", RestConstants.URI_PREFIX + moduleActionLink);
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("version");
			description.addProperty("started");
			description.addProperty("startupErrorMessage");
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			description.addLink("ref", ".?v=" + RestConstants.REPRESENTATION_REF);
			description.addLink("action", RestConstants.URI_PREFIX + moduleActionLink);
			description.addSelfLink();
			return description;
		} else if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<Module> doGetAll(RequestContext context) throws ResponseException {
		moduleFactoryWrapper.checkPrivilege();
		return new NeedsPaging<Module>(new ArrayList<Module>(moduleFactoryWrapper.getLoadedModules()), context);
	}
	
	@PropertyGetter("uuid")
	public static String getUuid(Module instance) {
		return instance.getModuleId();
	}
	
	@PropertyGetter("display")
	public static String getDisplay(Module instance) {
		return instance.getName();
	}
	
	@Override
	public Object upload(MultipartFile file, RequestContext context) throws ResponseException, IOException {
		moduleFactoryWrapper.checkPrivilege();
		
		File moduleFile = null;
		Module module = null;
		
		try {
			if (file == null || file.isEmpty()) {
				throw new IllegalArgumentException("Uploaded OMOD file cannot be empty");
			} else {
				String filename = WebUtil.stripFilename(file.getOriginalFilename());
				Module tmpModule = moduleFactoryWrapper.parseModuleFile(file);
				Module existingModule = moduleFactoryWrapper.getModuleById(tmpModule.getModuleId());
				ServletContext servletContext = context.getRequest().getSession().getServletContext();
				
				if (existingModule != null) {
					List<Module> dependentModulesStopped = moduleFactoryWrapper.stopModuleAndGetDependent(existingModule);
					
					for (Module depMod : dependentModulesStopped) {
						moduleFactoryWrapper.stopModuleSkipRefresh(depMod, servletContext);
					}
					
					moduleFactoryWrapper.stopModuleSkipRefresh(existingModule, servletContext);
					moduleFactoryWrapper.unloadModule(existingModule);
				}
				
				moduleFile = moduleFactoryWrapper.insertModuleFile(tmpModule, filename);
				module = moduleFactoryWrapper.loadModule(moduleFile);
				moduleFactoryWrapper.startModule(module, servletContext);
				return getByUniqueId(tmpModule.getModuleId());
			}
		}
		finally {
			if (module == null && moduleFile != null) {
				FileUtils.deleteQuietly(moduleFile);
			}
		}
	}
}

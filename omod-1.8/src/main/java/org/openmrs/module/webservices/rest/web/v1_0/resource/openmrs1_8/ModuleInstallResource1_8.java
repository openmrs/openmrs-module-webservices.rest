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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.util.ResourceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openmrs.module.Module;
import org.openmrs.module.ModuleUtil;
import org.openmrs.module.webservices.helper.ModuleInstall;
import org.openmrs.module.webservices.helper.ModuleFactoryWrapper;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Creatable;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.IllegalRequestException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + "/moduleinstall", supportedClass = ModuleInstall.class, supportedOpenmrsVersions = {
        "1.10.*", "1.11.*", "1.12.*", "1.8.*", "1.9.*", "2.0.*", "2.1.*" })
public class ModuleInstallResource1_8 extends BaseDelegatingResource<ModuleInstall> implements Creatable {
	
	private ModuleFactoryWrapper moduleFactoryWrapper = new ModuleFactoryWrapper();
	
	public void setModuleFactoryWrapper(ModuleFactoryWrapper moduleFactoryWrapper) {
		this.moduleFactoryWrapper = moduleFactoryWrapper;
	}
	
	@Override
	public Object create(SimpleObject post, RequestContext context) throws ResponseException {
		moduleFactoryWrapper.checkPrivilege();
		ModuleInstall moduleInstall = newDelegate();
		setConvertedProperties(moduleInstall, post, getCreatableProperties(), true);
		
		String moduleUuid = moduleInstall.getModuleUuid();
		String installUri = moduleInstall.getInstallUri();
		Module module = null;
		File moduleFile = null;
		
		ServletContext servletContext = getServletContext(context);
		
		if (moduleUuid == null && installUri == null) {
			throw new IllegalRequestException("The moduleUuid and installUri is needed to perform this action");
		}
		
		if (ResourceUtils.isUrl(installUri)) {
			try {
				Module existingModule = moduleFactoryWrapper.getModuleById(moduleUuid);
				if (existingModule != null) {
					List<Module> dependentModulesStopped = moduleFactoryWrapper.stopModuleAndGetDependent(existingModule);
					
					for (Module depMod : dependentModulesStopped) {
						moduleFactoryWrapper.stopModuleSkipRefresh(depMod, servletContext);
					}
					
					moduleFactoryWrapper.stopModuleSkipRefresh(existingModule, servletContext);
					moduleFactoryWrapper.unloadModule(existingModule);
				}
				
				URL downloadUrl = new URL(installUri);
				String fileName = FilenameUtils.getName(downloadUrl.getPath());
				InputStream inputStream = ModuleUtil.getURLStream(downloadUrl);
				moduleFile = ModuleUtil.insertModuleFile(inputStream, fileName);
				module = moduleFactoryWrapper.loadModule(moduleFile);
				moduleFactoryWrapper.startModule(module, servletContext);
				
				return ConversionUtil.convertToRepresentation(moduleInstall, Representation.DEFAULT);
			}
			catch (MalformedURLException e) {
				throw new RuntimeException(e.getMessage());
			}
			catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
			finally {
				if (module == null && moduleFile != null) {
					FileUtils.deleteQuietly(moduleFile);
				}
			}
		}
		
		throw new IllegalRequestException("The installUri needs to be a URL for this action to be performed");
	}
	
	@Override
	public String getUri(Object instance) {
		return null;
	}
	
	@Override
	public ModuleInstall newDelegate() {
		return new ModuleInstall();
	}
	
	@Override
	public ModuleInstall save(ModuleInstall delegate) {
		throw new UnsupportedOperationException("ModuleInstall can not be saved");
	}
	
	@Override
	public ModuleInstall getByUniqueId(String uniqueId) {
		throw new UnsupportedOperationException("ModuleInstall can not be saved");
	}
	
	@Override
	protected void delete(ModuleInstall delegate, String reason, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("ModuleInstall can not be deleted");
	}
	
	@Override
	public void purge(ModuleInstall delegate, RequestContext context) throws ResponseException {
		throw new UnsupportedOperationException("ModuleInstall can not be purged");
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("moduleUuid");
		description.addProperty("installUri");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("moduleUuid");
		description.addProperty("installUri");
		return description;
	}
	
	private ServletContext getServletContext(RequestContext context) {
		return context.getRequest().getSession().getServletContext();
	}
}

/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs2_0;

import io.swagger.models.Model;
import org.openmrs.layout.name.NameSupport;
import org.openmrs.layout.name.NameTemplate;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingReadableResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.GenericRestException;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.v1_0.helper.LayoutTemplateProvider;

import static org.openmrs.util.OpenmrsConstants.GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT;

/**
 * {@link Resource} for {@link NameTemplate}, supporting Read operation.
 */
@Resource(name = RestConstants.VERSION_1 + "/nametemplate", supportedClass = NameTemplate.class, supportedOpenmrsVersions = {
		"2.0.* - 9.*" })
public class NameTemplateResource2_0 extends BaseDelegatingReadableResource<NameTemplate> {
	
	/* special Uuid to reference the name template specified by the global property layout.name.format */
	public static final String GLOBAL_NAME_TEMPLATE = GLOBAL_PROPERTY_LAYOUT_NAME_FORMAT;
	
	/* name of the global property used to apply default values to name templates */
	public static final String LAYOUT_NAME_DEFAULTS = "layout.name.defaults";
	
	enum NameTemplateTokenEnum {
		IS_NOT_NAME_TOKEN,
		IS_NAME_TOKEN
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		return getTemplateProvider().getRepresentationDescription(rep);
	}
	
	@Override
	public Model getGETModel(Representation rep) {
		return getTemplateProvider().getGETModel(NameTemplateTokenEnum.class);
	}
	
	@Override
	public PageableResult doGetAll(RequestContext context) {
		try {
			return new NeedsPaging<>(getTemplateProvider().getAllLayoutTemplates(), context);
		}
		catch (Exception e) {
			throw new GenericRestException("Exception while getting name templates", e);
		}
	}
	
	@Override
	public NameTemplate getByUniqueId(String codename) {
		NameTemplate nameTemplate;
		/* special-case handling for GET /nametemplate/layout.name.format;
		   return the system-configured default name template. */
		if (codename.equalsIgnoreCase(GLOBAL_NAME_TEMPLATE)) {
			nameTemplate = getTemplateProvider().getDefaultLayoutTemplate();
		}
		else {
			nameTemplate = getTemplateProvider().getLayoutTemplateByName(codename);
		}
		if (nameTemplate == null) {
			throw new ObjectNotFoundException("NameTemplate with codename: " + codename + " doesn't exist.");
		}
		return nameTemplate;
	}
	
	@Override
	public NameTemplate newDelegate() {
		return null;
	}
	
	@Override
	protected String getUniqueId(NameTemplate delegate) {
		return delegate.getCodeName();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return RestConstants2_0.RESOURCE_VERSION;
	}
	
	private static LayoutTemplateProvider<NameTemplate> getTemplateProvider() {
		return new LayoutTemplateProvider<>(NameSupport.getInstance(), LAYOUT_NAME_DEFAULTS);
	}
}

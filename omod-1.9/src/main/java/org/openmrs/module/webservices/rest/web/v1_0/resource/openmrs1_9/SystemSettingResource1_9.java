/**
 * The contents of this file are subject to the OpenMRS Public License Version
 * 1.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * Copyright (C) OpenMRS, LLC. All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openmrs.GlobalProperty;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link GlobalProperty}, supporting standard CRUD
 * operations
 */
@Resource(name = RestConstants.VERSION_1 + "/systemsetting", supportedClass = GlobalProperty.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*"})
public class SystemSettingResource1_9 extends DelegatingCrudResource<GlobalProperty> {

    public static final String GENERAL = "General Settings";

    @RepHandler(RefRepresentation.class)
    public SimpleObject asRef(GlobalProperty delegate) throws ConversionException {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("property");
        description.addProperty("propertyValue");
        description.addProperty("description");
        description.addProperty("display", findMethod("getDisplayName"));
        description.addSelfLink();
        return convertDelegateToRepresentation(delegate, description);
    }

    @RepHandler(FullRepresentation.class)
    public SimpleObject asFull(GlobalProperty delegate) throws ConversionException {
        DelegatingResourceDescription description = fullRepresentationDescription(delegate);
        return convertDelegateToRepresentation(delegate, description);
    }

    protected DelegatingResourceDescription fullRepresentationDescription(GlobalProperty delegate) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("property");
        description.addProperty("propertyValue");
        description.addProperty("description");
        description.addProperty("display", findMethod("getDisplayName"));
        description.addProperty("datatypeClassname");
        description.addProperty("datatypeConfig");
        description.addProperty("preferredHandlerClassname");
        description.addProperty("handlerConfig");
        description.addSelfLink();
        return description;
    }

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("property");
            description.addProperty("propertyValue");
            description.addProperty("description");
            description.addProperty("display", findMethod("getDisplayName"));
            description.addProperty("datatypeClassname");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        }
        return null;
    }

    /**
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
     */
    @Override
    public DelegatingResourceDescription getCreatableProperties() {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addRequiredProperty("property");
        description.addRequiredProperty("propertyValue");
        description.addRequiredProperty("description");
        description.addRequiredProperty("datatypeClassname");
        return description;
    }

    /**
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
     */
    @Override
    public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
        DelegatingResourceDescription description = super.getUpdatableProperties();
        description.addRequiredProperty("property");
        description.addRequiredProperty("propertyValue");
        description.addRequiredProperty("description");
        description.addRequiredProperty("datatypeClassname");
        return description;
    }

    /**
     * @see DelegatingCrudResource#newDelegate()
     */
    @Override
    public GlobalProperty newDelegate() {
        return new GlobalProperty();
    }

    /**
     * @see DelegatingCrudResource#save(java.lang.Object)
     */
    @Override
    public GlobalProperty save(GlobalProperty property) {
        return Context.getAdministrationService().saveGlobalProperty(property);
    }

    /**
     * Fetches a global property by uuid
     *
     * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
     */
    @Override
    public GlobalProperty getByUniqueId(String uuid) {
        return Context.getAdministrationService().getGlobalPropertyByUuid(uuid);
    }

    /**
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#delete(java.lang.Object,
     * java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected void delete(GlobalProperty property, String reason, RequestContext context) throws ResponseException {
        if (property == null) {
            return;
        }
        Context.getAdministrationService().purgeGlobalProperty(property);
    }

    /**
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
     * org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    public void purge(GlobalProperty property, RequestContext context) throws ResponseException {
        if (property == null) {
            return;
        }
        Context.getAdministrationService().purgeGlobalProperty(property);
    }

    /**
     * Get all the global properties
     *
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
     */
    @Override
    protected NeedsPaging<GlobalProperty> doGetAll(RequestContext context) {
        List<GlobalProperty> allGlobalPropertys = Context.getAdministrationService().getAllGlobalProperties();
        return new NeedsPaging<GlobalProperty>(allGlobalPropertys, context);
    }

    /**
     * GlobalProperty searches support the following additional query
     * parameters:
     *
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
     */
    @Override
    protected PageableResult doSearch(RequestContext context) {
        AdministrationService service = Context.getAdministrationService();
        List<GlobalProperty> searchResults;
        searchResults = service.getGlobalPropertiesByPrefix(context.getParameter("q"));
        PageableResult result = new NeedsPaging<GlobalProperty>(searchResults, context);
        return result;
    }

    /**
     * @see
     * org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
     */
    @Override
    public String getResourceVersion() {
        return "1.9";
    }

    /**
     * Gets the display name of the global property delegate
     *
     * @param instance the delegate instance to get the display name off
     */
    public String getDisplayName(GlobalProperty globalProperty) {
        String section = GENERAL;
        String name = globalProperty.getProperty();
        int sectionEnd = globalProperty.getProperty().indexOf(".");
        if (sectionEnd > 0) {
            section = globalProperty.getProperty().substring(0, sectionEnd);
            section = beautify(section);
            name = globalProperty.getProperty().substring(sectionEnd + 1);
            name = beautify(name);
        }
        return section + " - " + name + " - " + globalProperty.getPropertyValue();
    }

    /**
     * Beautifies string
     *
     * @param section
     * @return
     */
    private String beautify(String section) {
        section = section.replace("_", " ");
        section = section.replace(".", " ");

        String[] sections = StringUtils.splitByCharacterTypeCamelCase(section);
        section = StringUtils.join(sections, " ");

        sections = StringUtils.split(section);
        for (int i = 0; i < sections.length; i++) {
            sections[i] = StringUtils.capitalize(sections[i]);
        }
        section = StringUtils.join(sections, " ");

        return section;
    }

}

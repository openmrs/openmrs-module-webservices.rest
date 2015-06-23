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
import org.openmrs.customdatatype.CustomDatatype;
import org.openmrs.customdatatype.CustomDatatypeUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link GlobalProperty}, supporting standard CRUD
 * operations
 */
@Resource(name = RestConstants.VERSION_1 + "/systemsetting", supportedClass = GlobalProperty.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*"})
public class SystemSettingResource1_9 extends DelegatingCrudResource<GlobalProperty> {

    public static final String GENERAL = "General Settings";

    /**
     * @see DelegatingCrudResource#getRepresentationDescription(Representation)
     */
    @Override
    public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
        if (rep instanceof RefRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("display");
            description.addSelfLink();
            return description;
        } else if (rep instanceof DefaultRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("property");
            description.addProperty("value");
            description.addProperty("description");
            description.addProperty("display");
            description.addProperty("datatypeClassname");
            description.addSelfLink();
            description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
            return description;
        } else if (rep instanceof FullRepresentation) {
            DelegatingResourceDescription description = new DelegatingResourceDescription();
            description.addProperty("uuid");
            description.addProperty("property");
            description.addProperty("value");
            description.addProperty("description");
            description.addProperty("display");
            description.addProperty("datatypeClassname");
            description.addProperty("datatypeConfig");
            description.addProperty("preferredHandlerClassname");
            description.addProperty("handlerConfig");
            description.addSelfLink();
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
        description.addRequiredProperty("value");
        description.addRequiredProperty("description");
        description.addProperty("datatypeClassname");
        description.addProperty("datatypeConfig");
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
        description.addRequiredProperty("value");
        description.addRequiredProperty("description");
        description.addProperty("datatypeClassname");
        description.addProperty("datatypeConfig");
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
    @PropertyGetter("display")
    public String getDisplayString(GlobalProperty globalProperty) {
        String section = GENERAL;
        String name = globalProperty.getProperty();
        int sectionEnd = globalProperty.getProperty().indexOf(".");
        if (sectionEnd > 0) {
            section = globalProperty.getProperty().substring(0, sectionEnd);
            section = beautify(section);
            name = globalProperty.getProperty().substring(sectionEnd + 1);
            name = beautify(name);
        }
        return section + " - " + name + " = " + globalProperty.getValue();
    }

    /**
     * Sets value for given property.
     *
     * @param property
     * @param value
     */
    @PropertySetter("value")
    public static void setValue(GlobalProperty property, String value) throws Exception {
        if (StringUtils.isNotEmpty(property.getDatatypeClassname()) && StringUtils.isNotEmpty(property.getDatatypeConfig())) {
            CustomDatatype customDataType = CustomDatatypeUtil.getDatatype(property.getDatatypeClassname(), property.getDatatypeConfig());
            if (customDataType != null) {
                try {
                    property.setValue(customDataType.fromReferenceString(value).toString());
                } catch (Exception ex) {
                    property.setPropertyValue(value);
                }
            }
        } else {
            property.setPropertyValue(value);
        }
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

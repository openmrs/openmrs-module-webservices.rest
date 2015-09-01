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
import org.openmrs.api.APIException;
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
import org.openmrs.web.controller.maintenance.SettingsProperty;

/**
 * {@link Resource} for {@link GlobalProperty}, supporting standard CRUD
 * operations
 */
@Resource(name = RestConstants.VERSION_1 + "/systemsetting", supportedClass = GlobalProperty.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*","1.12.*"})
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
        description.addRequiredProperty("description");
        description.addProperty("datatypeClassname");
        description.addProperty("datatypeConfig");
        description.addProperty("preferredHandlerClassname");
        description.addProperty("handlerConfig");
        description.addRequiredProperty("value");
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
        description.addRequiredProperty("description");
        description.addProperty("datatypeClassname");
        description.addProperty("datatypeConfig");
        description.addProperty("preferredHandlerClassname");
        description.addProperty("handlerConfig");
        description.addRequiredProperty("value");
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
     * @return string as "section - name = value"
     */
    @PropertyGetter("display")
    public static String getDisplayString(GlobalProperty globalProperty) {
        SettingsProperty property = new SettingsProperty(globalProperty);
        return property.getSection() + " - " + property.getName() + " = " + globalProperty.getValue();
    }

    /**
     * Gets the value of the global property delegate
     *
     * @param instance the delegate instance to get the value off
     * @return value object
     */
    @PropertyGetter("value")
    public static Object getValue(GlobalProperty globalProperty) {
        if (StringUtils.isNotEmpty(globalProperty.getDatatypeClassname()) && StringUtils.isNotEmpty(globalProperty.getDatatypeConfig()))
            return globalProperty.getValue();
        else
            return globalProperty.getPropertyValue();
            
    }
    
    /**
     * Sets value for given property.
     *
     * @param property
     * @param value
     */
    @PropertySetter("value")
    public static void setValue(GlobalProperty property, String value) throws Exception {
        if (StringUtils.isNotEmpty(property.getDatatypeClassname())) {
            CustomDatatype customDataType = CustomDatatypeUtil.getDatatype(property.getDatatypeClassname(), property.getDatatypeConfig());
            if (customDataType != null) {
                try {
                    property.setValue(customDataType.fromReferenceString(value));
                } catch (Exception ex) {
                    throw new APIException("Exception in converting value to custom data type", ex);
                }
            }
            else
                throw new APIException("Custom data type is null as per provided parameters");
        } else {
            property.setPropertyValue(value);
        }
    }
}

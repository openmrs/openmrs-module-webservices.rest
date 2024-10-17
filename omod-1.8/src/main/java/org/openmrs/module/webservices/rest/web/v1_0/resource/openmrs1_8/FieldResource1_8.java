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

import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.Concept;
import org.openmrs.Field;
import org.openmrs.FieldType;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;
import java.util.List;

/**
 * {@link Resource} for {@link Field}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/field", supportedClass = Field.class, supportedOpenmrsVersions = { "1.8.* - 9.*" })
public class FieldResource1_8 extends MetadataDelegatingCrudResource<Field> {
	
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> modelImpl = super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			modelImpl
					.addProperty("tableName", new StringSchema())
					.addProperty("attributeName", new StringSchema())
					.addProperty("defaultValue", new StringSchema())
					.addProperty("selectMultiple", new BooleanSchema()._default(false));
		}
		if (rep instanceof DefaultRepresentation) {
			modelImpl
					.addProperty("fieldType", new Schema<FieldType>().$ref("#/components/schemas/FieldtypeGet"))
					.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGet"));
		} else if (rep instanceof FullRepresentation) {
			modelImpl
					.addProperty("fieldType", new Schema<FieldType>().$ref("#/components/schemas/FieldtypeGetFull"))
					.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptGetFull"));
		}
		return modelImpl;
	}
	
	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		ObjectSchema schema = (ObjectSchema) super.getCREATESchema(rep)
				.addProperty("fieldType", new Schema<FieldType>().$ref("#/components/schemas/FieldtypeCreate"))
				.addProperty("selectMultiple", new BooleanSchema()._default(false))
				.addProperty("concept", new Schema<Concept>().$ref("#/components/schemas/ConceptCreate"))
				.addProperty("tableName", new StringSchema())
				.addProperty("attributeName", new StringSchema())
				.addProperty("defaultValue", new StringSchema());

		schema.setRequired(Arrays.asList("fieldType", "selectMultiple"));
		return schema;
	}

	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return super.getUPDATESchema(rep);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("fieldType", Representation.REF);
			description.addProperty("concept", Representation.REF);
			description.addProperty("tableName");
			description.addProperty("attributeName");
			description.addProperty("defaultValue");
			description.addProperty("selectMultiple");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("fieldType");
			description.addProperty("concept");
			description.addProperty("tableName");
			description.addProperty("attributeName");
			description.addProperty("defaultValue");
			description.addProperty("selectMultiple");
			description.addProperty("retired");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("fieldType");
		description.addRequiredProperty("selectMultiple");
		
		description.addProperty("concept");
		description.addProperty("tableName");
		description.addProperty("attributeName");
		description.addProperty("defaultValue");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Field getByUniqueId(String uniqueId) {
		return Context.getFormService().getFieldByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Field newDelegate() {
		return new Field();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Field save(Field delegate) {
		return Context.getFormService().saveField(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Field delegate, RequestContext context) throws ResponseException {
		if (delegate == null)
			return;
		Context.getFormService().purgeField(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Field> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Field>(Context.getFormService().getAllFields(context.getIncludeAll()), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("answers");
	}
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.NumberSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.Field;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for {@link FormField}, supporting standard CRUD operations
 */
@SubResource(parent = FormResource1_8.class, path = "formfield", supportedClass = FormField.class, supportedOpenmrsVersions = {
        "1.8.* - 9.*" })
public class FormFieldResource1_8 extends DelegatingSubResource<FormField, Form, FormResource1_8> {
	
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> modelImpl = super.getGETSchema(rep);
		if (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation) {
			modelImpl
			        .addProperty("uuid", new StringSchema())
			        .addProperty("display", new StringSchema())
			        .addProperty("fieldNumber", new IntegerSchema())
			        .addProperty("fieldPart", new StringSchema())
			        .addProperty("pageNumber", new IntegerSchema())
			        .addProperty("minOccurs", new IntegerSchema())
			        .addProperty("maxOccurs", new IntegerSchema())
			        .addProperty("required", new BooleanSchema()._default(false))
			        .addProperty("sortWeight", new NumberSchema().format("float"))
			        .addProperty("retired", new BooleanSchema());
		}
		if (rep instanceof DefaultRepresentation) {
			modelImpl
					.addProperty("form", new Schema<Form>().$ref("#/components/schemas/FormGet"))
					.addProperty("field", new Schema<Field>().$ref("#/components/schemas/FieldGet"))
					.addProperty("parent", new Schema<FormField>().$ref("#/components/schemas/FormFormfieldGet"));
		} else if (rep instanceof FullRepresentation) {
			modelImpl
					.addProperty("form", new Schema<Form>().$ref("#/components/schemas/FormGetFull"))
					.addProperty("field", new Schema<Field>().$ref("#/components/schemas/FieldGetFull"))
					.addProperty("parent", new Schema<FormField>().$ref("#/components/schemas/FormFormfieldGetFull"));
		}
		return modelImpl;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Schema<Object> getCREATESchema(Representation rep) {
		ObjectSchema model = (ObjectSchema) new ObjectSchema()
		        .addProperty("form", new StringSchema().example("uuid"))
		        .addProperty("field", new StringSchema().example("uuid"))
		        .addProperty("required", new BooleanSchema()._default(false))
		        .addProperty("parent", new StringSchema().example("uuid"))
		        .addProperty("fieldNumber", new IntegerSchema())
		        .addProperty("fieldPart", new StringSchema())
		        .addProperty("pageNumber", new IntegerSchema())
		        .addProperty("minOccurs", new IntegerSchema())
		        .addProperty("maxOccurs", new IntegerSchema())
		        .addProperty("sortWeight", new BooleanSchema()._default(false))
		        .required(Arrays.asList("form", "field", "required"));
		if (rep instanceof FullRepresentation) {
			model
					.addProperty("form", new Schema<Form>().$ref("#/components/schemas/FormCreate"))
					.addProperty("field", new Schema<Field>().$ref("#/components/schemas/FieldCreate"))
					.addProperty("parent", new Schema<FormField>().$ref("#/components/schemas/FormFormfieldCreate"));
		}
		return model;
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return new ObjectSchema(); //FIXME missing props
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
			description.addProperty("parent", Representation.REF);
			description.addProperty("form", Representation.REF);
			description.addProperty("field", Representation.REF);
			description.addProperty("fieldNumber");
			description.addProperty("fieldPart");
			description.addProperty("pageNumber");
			description.addProperty("minOccurs");
			description.addProperty("maxOccurs");
			description.addProperty("required");
			description.addProperty("sortWeight");
			description.addProperty("retired");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("parent");
			description.addProperty("form");
			description.addProperty("field");
			description.addProperty("fieldNumber");
			description.addProperty("fieldPart");
			description.addProperty("pageNumber");
			description.addProperty("minOccurs");
			description.addProperty("maxOccurs");
			description.addProperty("required");
			description.addProperty("sortWeight");
			description.addProperty("retired");
			description.addProperty("auditInfo");
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addRequiredProperty("form");
		description.addRequiredProperty("field");
		description.addRequiredProperty("required");
		
		description.addProperty("parent");
		description.addProperty("fieldNumber");
		description.addProperty("fieldPart");
		description.addProperty("pageNumber");
		description.addProperty("minOccurs");
		description.addProperty("maxOccurs");
		description.addProperty("sortWeight");
		
		return description;
	}
	
	/**
	 * Gets the display string.
	 * 
	 * @param formField the formField name object
	 * @return the display string
	 */
	@PropertyGetter("display")
	public String getDisplayString(FormField formField) {
		return formField.getName();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public FormField getByUniqueId(String uniqueId) {
		return Context.getFormService().getFormFieldByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public FormField newDelegate() {
		return new FormField();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public FormField save(FormField delegate) {
		return Context.getFormService().saveFormField(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(FormField delegate, RequestContext context) throws ResponseException {
		if (delegate == null)
			return;
		Context.getFormService().purgeFormField(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Form getParent(FormField instance) {
		return instance.getForm();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(FormField instance, Form parent) {
		instance.setForm(parent);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<FormField> doGetAll(Form parent, RequestContext context) throws ResponseException {
		List<FormField> formFields = new ArrayList<FormField>();
		for (FormField formField : parent.getFormFields()) {
			if (!formField.isRetired()) {
				formFields.add(formField);
			}
		}
		
		return new NeedsPaging<FormField>(formFields, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(FormField delegate, String reason, RequestContext context) throws ResponseException {
		delegate.setRetired(true);
		delegate.setRetireReason(reason);
		delegate.setRetiredBy(Context.getAuthenticatedUser());
		Context.getFormService().saveFormField(delegate);
	}
}

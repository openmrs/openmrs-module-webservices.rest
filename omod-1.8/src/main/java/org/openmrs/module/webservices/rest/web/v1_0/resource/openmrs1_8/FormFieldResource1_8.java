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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8;

import org.openmrs.Auditable;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Form;
import org.openmrs.FormField;
import org.openmrs.Retireable;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
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

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Resource} for {@link FormField}, supporting standard CRUD operations
 */
@SubResource(parent = FormResource1_8.class, path = "formfield", order = 200, supportedClass = FormField.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class FormFieldResource1_8 extends
		DelegatingSubResource<FormField, Form, FormResource1_8> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
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
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
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
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getAuditInfo(org.openmrs.BaseOpenmrsObject)
	 */
	@Override
	public SimpleObject getAuditInfo(BaseOpenmrsObject resource)
			throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(
				resource, "creator", Representation.REF));
		ret.put("dateCreated",
				ConversionUtil.convertToRepresentation(
						((Auditable) resource).getDateCreated(),
						Representation.DEFAULT));
		if (((Retireable) resource).isRetired()) {
			ret.put("retiredBy", ConversionUtil.getPropertyWithRepresentation(
					resource, "retiredBy", Representation.REF));
			ret.put("dateRetired", ConversionUtil.convertToRepresentation(
					((Retireable) resource).getDateRetired(),
					Representation.DEFAULT));
			ret.put("retireReason", ConversionUtil.convertToRepresentation(
					((Retireable) resource).getRetireReason(),
					Representation.DEFAULT));
		}
		return ret;
	}

	/**
	 * Gets the display string.
	 * 
	 * @param formField
	 *            the formField name object
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
	public void purge(FormField delegate, RequestContext context)
			throws ResponseException {
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
	public NeedsPaging<FormField> doGetAll(Form parent, RequestContext context)
			throws ResponseException {
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
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(FormField delegate, String reason,
			RequestContext context) throws ResponseException {
		delegate.setRetired(true);
		delegate.setRetireReason(reason);
		delegate.setRetiredBy(Context.getAuthenticatedUser());
		Context.getFormService().saveFormField(delegate);
	}
}

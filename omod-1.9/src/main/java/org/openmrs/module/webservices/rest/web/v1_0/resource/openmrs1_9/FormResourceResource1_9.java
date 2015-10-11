package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Form;
import org.openmrs.FormResource;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_8.FormResource1_8;

import java.util.List;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@SubResource(parent = FormResource1_8.class, path = "resource", order = 100, supportedClass = FormResource.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class FormResourceResource1_9 extends
		DelegatingSubResource<FormResource, Form, FormResource1_8> {

	@RepHandler(RefRepresentation.class)
	public SimpleObject convertToRef(FormResource delegate)
			throws ConversionException {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("display");
		addValueLink(rep, delegate);
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}

	@RepHandler(DefaultRepresentation.class)
	public SimpleObject asDefaultRep(FormResource delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("valueReference");
		rep.addProperty("display");
		addValueLink(rep, delegate);
		rep.addSelfLink();
		rep.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
		return convertDelegateToRepresentation(delegate, rep);
	}

	@RepHandler(FullRepresentation.class)
	public SimpleObject asFullRep(FormResource delegate) throws Exception {
		DelegatingResourceDescription rep = new DelegatingResourceDescription();
		rep.addProperty("uuid");
		rep.addProperty("name");
		rep.addProperty("valueReference");
		rep.addProperty("dataType");
		rep.addProperty("handler");
		rep.addProperty("handlerConfig");
		rep.addProperty("display");
		addValueLink(rep, delegate);
		rep.addSelfLink();
		return convertDelegateToRepresentation(delegate, rep);
	}

	@PropertyGetter("display")
	public String getDisplayString(FormResource delegate) {
		return StringUtils.isEmpty(delegate.getName()) ? "" : delegate
				.getName();
	}

	@PropertyGetter("dataType")
	public String getDataType(FormResource formResource) {
		return formResource.getDatatypeClassname();
	}

	@PropertySetter("dataType")
	public void setDataType(FormResource formResource, String dataType) {
		formResource.setDatatypeClassname(dataType);
	}

	@PropertyGetter("handler")
	public String getHandler(FormResource formResource) {
		return formResource.getPreferredHandlerClassname();
	}

	@PropertySetter("handler")
	public void setHandler(FormResource formResource, String handler) {
		formResource.setPreferredHandlerClassname(handler);
	}

	@PropertyGetter("valueReference")
	public String getValueReference(FormResource formResource) {
		try {
			return formResource.getValueReference();
		} catch (Exception e) {
			return null;
		}
	}

	@PropertySetter("valueReference")
	public void setValueReference(FormResource formResource,
			String valueReference) {
		formResource.setValueReferenceInternal(valueReference);
	}

	@Override
	public FormResource getByUniqueId(String uniqueId) {
		return Context.getFormService().getFormResourceByUuid(uniqueId);
	}

	@Override
	public DelegatingResourceDescription getCreatableProperties()
			throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("form");
		description.addProperty("name");
		description.addProperty("dataType");
		description.addProperty("handler");
		description.addProperty("handlerConfig");
		description.addProperty("value");
		description.addProperty("valueReference");

		return description;
	}

	@Override
	protected void delete(FormResource delegate, String reason,
			RequestContext context) throws ResponseException {
		purge(delegate, context);
	}

	@Override
	public void purge(FormResource delegate, RequestContext context)
			throws ResponseException {
		Context.getFormService().purgeFormResource(delegate);
	}

	@Override
	public FormResource newDelegate() {
		return new FormResource();
	}

	@Override
	public FormResource save(FormResource delegate) {
		return Context.getFormService().saveFormResource(delegate);
	}

	@Override
	public String getResourceVersion() {
		return RestConstants1_9.RESOURCE_VERSION;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		// Returning null causes the @RepHandler to be called.
		return null;
	}

	@Override
	public Form getParent(FormResource instance) {
		return instance.getForm();
	}

	@Override
	public void setParent(FormResource instance, Form parent) {
		instance.setForm(parent);
	}

	@Override
	public PageableResult doGetAll(Form parent, RequestContext context)
			throws ResponseException {
		List<FormResource> resources = (List<FormResource>) Context
				.getFormService().getFormResourcesForForm(parent);
		return new NeedsPaging<FormResource>(resources, context);
	}

	private void addValueLink(DelegatingResourceDescription rep,
			FormResource delegate) {
		rep.addLink("value", "./value");
	}
}

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

import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import org.openmrs.EncounterType;
import org.openmrs.Form;
import org.openmrs.FormField;
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
 * {@link Resource} for {@link Form}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/form", supportedClass = Form.class, supportedOpenmrsVersions = { "1.8.* - 9.*" })
public class FormResource1_8 extends MetadataDelegatingCrudResource<Form> {
	
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
			description.addProperty("encounterType", Representation.REF);
			description.addProperty("version");
			description.addProperty("build");
			description.addProperty("published");
			description.addProperty("formFields", Representation.REF);
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
			description.addProperty("encounterType");
			description.addProperty("version");
			description.addProperty("build");
			description.addProperty("published");
			description.addProperty("formFields");
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
		DelegatingResourceDescription description = super.getCreatableProperties();
		description.addRequiredProperty("version");
		
		description.addProperty("encounterType");
		description.addProperty("build");
		description.addProperty("published");
		description.addProperty("formFields");
		description.addProperty("xslt");
		description.addProperty("template");
		
		return description;
	}

	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = super.getGETSchema(rep);
		if (schema instanceof ObjectSchema && (rep instanceof DefaultRepresentation || rep instanceof FullRepresentation)) {
			ObjectSchema objectSchema = (ObjectSchema) schema;
			objectSchema
					.addProperty("uuid", new StringSchema())
					.addProperty("display", new StringSchema())
					.addProperty("name", new StringSchema())
					.addProperty("description", new StringSchema())
					.addProperty("version", new StringSchema())
					.addProperty("build", new IntegerSchema())
					.addProperty("published", new BooleanSchema()._default(false))
					.addProperty("retired", new BooleanSchema());

			if (rep instanceof DefaultRepresentation) {
				objectSchema
						.addProperty("encounterType", new Schema<EncounterType>().$ref("#/components/schemas/EncountertypeGetRef"))
						.addProperty("formFields", new ArraySchema().items(new Schema<FormField>().$ref("#/components/schemas/FormFormfieldGetRef")));
			} else if (rep instanceof FullRepresentation) {
				objectSchema
						.addProperty("encounterType", new Schema<EncounterType>().$ref("#/components/schemas/EncountertypeGetRef"))
						.addProperty("formFields", new ArraySchema().items(new Schema<FormField>().$ref("#/components/schemas/FormFormfieldGetRef")));;
			}
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> schema = super.getCREATESchema(rep);
		if (schema instanceof ObjectSchema) {
			ObjectSchema objectSchema = (ObjectSchema) schema;
			objectSchema
					.addProperty("version", new StringSchema())
					.addProperty("encounterType", new StringSchema())
					.addProperty("build", new IntegerSchema())
					.addProperty("published", new BooleanSchema()._default(false))
					.addProperty("formFields", new ArraySchema().items(new StringSchema()))
					.addProperty("xslt", new StringSchema())
					.addProperty("template", new StringSchema());

			objectSchema.setRequired(Arrays.asList("version"));

			if (rep instanceof FullRepresentation) {
				objectSchema
						.addProperty("encounterType", new Schema<EncounterType>().$ref("#/components/schemas/EncountertypeCreate"))
						.addProperty("formFields", new ArraySchema().items(new Schema<FormField>().$ref("#/components/schemas/FormFormfieldCreate")));
			}
		}
		return schema;
	}
	
	@Override
	public Schema<?> getUPDATESchema(Representation rep) {
		return getCREATESchema(rep);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Form getByUniqueId(String uniqueId) {
		return Context.getFormService().getFormByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public Form newDelegate() {
		return new Form();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public Form save(Form delegate) {
		return Context.getFormService().saveForm(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Form delegate, RequestContext context) throws ResponseException {
		if (delegate == null)
			return;
		Context.getFormService().purgeForm(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Form> doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<Form>(Context.getFormService().getAllForms(context.getIncludeAll()), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Form> doSearch(RequestContext context) {
		String fuzzyName = context.getParameter("q");
		boolean includeRetired = context.getIncludeAll();

		return new NeedsPaging<Form>(Context.getFormService().getForms(fuzzyName, null, null,
				includeRetired, null, null, null), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("formFields");
	}
	
}

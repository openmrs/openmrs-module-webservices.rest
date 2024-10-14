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

import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.BooleanSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import org.openmrs.Obs;
import org.openmrs.Person;
import org.openmrs.activelist.ActiveListItem;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.Arrays;

/**
 * Subclass of {@link DataDelegatingCrudResource} with helper methods specific to
 * {@link ActiveListItem}
 */
public abstract class BaseActiveListItemResource1_8<T extends ActiveListItem> extends DataDelegatingCrudResource<T> {
	
	@Override
	public Schema<?> getGETSchema(Representation rep) {
		Schema<?> schema = new ObjectSchema()
				.addProperty("uuid", new StringSchema())
				.addProperty("display", new StringSchema())
				.addProperty("startDate", new DateTimeSchema())
				.addProperty("endDate", new DateTimeSchema())
				.addProperty("comments", new StringSchema())
				.addProperty("voided", new BooleanSchema());
		
		if (rep instanceof DefaultRepresentation) {
			schema
				.addProperty("person", new Schema<Object>().$ref("#/components/schemas/PersonGet"))
				.addProperty("activeListType", new StringSchema())
				.addProperty("startObs", new Schema<Object>().$ref("#/components/schemas/ObsGet"))
				.addProperty("stopObs", new Schema<Object>().$ref("#/components/schemas/ObsGetRef"));
		} else if (rep instanceof FullRepresentation) {
			schema
				.addProperty("person", new Schema<Object>().$ref("#/components/schemas/PersonGetRef"))
				.addProperty("activeListType", new StringSchema())
				.addProperty("startObs", new Schema<Object>().$ref("#/components/schemas/ObsGetRef"))
				.addProperty("stopObs", new Schema<Object>().$ref("#/components/schemas/ObsGetRef"));
		}
		return schema;
	}

	@Override
	public Schema<?> getCREATESchema(Representation rep) {
		Schema<?> schema = new ObjectSchema()
				.addProperty("person", new StringSchema())
				.addProperty("startDate", new DateSchema())
				.addProperty("comments", new StringSchema())
				.addProperty("startObs", new StringSchema())
				.addProperty("stopObs", new StringSchema());

		schema.setRequired(Arrays.asList("person", "startDate"));

		if (rep instanceof FullRepresentation) {
			schema.addProperty("person", new Schema<Person>().$ref("#/components/schemas/PersonCreate"));
			schema.addProperty("startObs", new Schema<Obs>().$ref("#/components/schemas/ObsCreate"));
			schema.addProperty("stopObs", new Schema<Obs>().$ref("#/components/schemas/ObsCreate"));
		}

		return schema;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("person", Representation.REF);
			description.addProperty("activeListType", Representation.REF);
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("startObs", Representation.REF);
			description.addProperty("stopObs", Representation.REF);
			description.addProperty("comments");
			description.addProperty("voided");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("person", Representation.REF);
			description.addProperty("activeListType", Representation.REF);
			description.addProperty("startDate");
			description.addProperty("endDate");
			description.addProperty("startObs", Representation.REF);
			description.addProperty("stopObs", Representation.REF);
			description.addProperty("comments");
			description.addProperty("voided");
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
		description.addRequiredProperty("person");
		description.addRequiredProperty("startDate");
		description.addProperty("comments");
		description.addProperty("startObs");
		description.addProperty("stopObs");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T getByUniqueId(String uniqueId) {
		return (T) Context.getActiveListService().getActiveListItemByUuid(uniqueId);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T save(T delegate) {
		return (T) Context.getActiveListService().saveActiveListItem(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(T delegate, String reason, RequestContext context) throws ResponseException {
		if (delegate.isVoided()) {
			// DELETE is idempotent, so we return success here
			return;
		}
		Context.getActiveListService().voidActiveListItem(delegate, reason);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(T delegate, RequestContext context) throws ResponseException {
		// TODO: add a purge method to core (TRUNK-3725), then add this here in restws (RESTWS-295)
		//Context.getActiveListService().purgeActiveListItem(delegate);
	}
	
	// TODO: add these lines into a "stop" method call.  RESTWS-296
	//delegate.setComments("Stopped from a REST web service call");
	//Context.getActiveListService().removeActiveListItem(delegate, null);
	
}
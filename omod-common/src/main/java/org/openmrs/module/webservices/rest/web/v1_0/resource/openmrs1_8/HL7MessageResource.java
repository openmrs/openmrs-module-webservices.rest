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

import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.util.IncomingHl7Message;

/**
 * {@link Resource} for {@link IncomingHl7Message}, supporting standard CRUD operations
 */
@Resource(name = "hl7", supportedClass = IncomingHl7Message.class)
public class HL7MessageResource extends DataDelegatingCrudResource<IncomingHl7Message> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(IncomingHl7Message delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public IncomingHl7Message getByUniqueId(String uniqueId) {
		// Currently it's not supported because we don't have methods within HL7 service, which are returning hl7 message
		// by its uuid. It will be fixed when such methods will be implemented 
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("messageState");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayString"));
			description.addProperty("source", Representation.DEFAULT);
			description.addProperty("sourceKey");
			description.addProperty("data");
			description.addProperty("messageState");
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
		description.addRequiredProperty("hl7");
		return description;
	}
	
	/**
	 * It needs to be overwritten to allow for hidden properties: source, sourceKey and data. They
	 * are automatically extracted from the hl7 property and populated in
	 * {@link HL7MessageController#create(String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
	 * . They should not be POSTed by the user.
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#setConvertedProperties(java.lang.Object,
	 *      java.util.Map,
	 *      org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription,
	 *      boolean)
	 */
	@Override
	protected void setConvertedProperties(IncomingHl7Message delegate, Map<String, Object> propertyMap,
	        DelegatingResourceDescription description, boolean mustIncludeRequiredProperties) throws ConversionException {
		for (Map.Entry<String, Object> prop : propertyMap.entrySet()) {
			setProperty(delegate, prop.getKey(), prop.getValue());
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public IncomingHl7Message newDelegate() {
		return new IncomingHl7Message();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(IncomingHl7Message delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceHandler#save(java.lang.Object)
	 */
	@Override
	public IncomingHl7Message save(IncomingHl7Message delegate) {
		return new IncomingHl7Message(Context.getHL7Service().saveHL7InQueue(delegate.toHL7InQueue()));
	}
	
	/**
	 * Gets the display string for an incoming hl7 message resource.
	 * 
	 * @param message the hl7 message.
	 * @return the display string.
	 */
	public String getDisplayString(IncomingHl7Message message) {
		return message.getSourceKey();
	}
}

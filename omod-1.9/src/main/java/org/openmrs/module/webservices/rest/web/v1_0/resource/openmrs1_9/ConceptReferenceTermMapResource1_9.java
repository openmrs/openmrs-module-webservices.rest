/**
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
package org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_9;

import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptReferenceTermMap;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
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
 * {@link Resource} for {@link ConceptReferenceTermMap}, supporting standard CRUD operations
 */
@Resource(name = RestConstants.VERSION_1 + "/conceptreferencetermmap", supportedClass = ConceptReferenceTermMap.class, supportedOpenmrsVersions = {"1.9.*", "1.10.*", "1.11.*", "1.12.*"})
public class ConceptReferenceTermMapResource1_9 extends DelegatingCrudResource<ConceptReferenceTermMap> {
	
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
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("termA", Representation.REF);
			description.addProperty("termB", Representation.REF);
			description.addProperty("conceptMapType", Representation.REF);
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("termA", Representation.DEFAULT);
			description.addProperty("termB", Representation.DEFAULT);
			description.addProperty("conceptMapType", Representation.DEFAULT);
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
		description.addRequiredProperty("termA");
		description.addRequiredProperty("termB");
		description.addRequiredProperty("conceptMapType");
		
		return description;
	}
	
	/**
	 * Gets the display string for a concept map.
	 * 
	 * @param conceptReferenceTermMap the concept map object.
	 * @return the display string.
	 */
	@PropertyGetter("display")
	public String getDisplayString(ConceptReferenceTermMap conceptReferenceTermMap) {
		if (conceptReferenceTermMap.getTermA() == null || conceptReferenceTermMap.getTermB() == null) {
			return "";
		}
		
		return conceptReferenceTermMap.getTermA().getConceptSource().getName() + ": "
		        + conceptReferenceTermMap.getTermA().getCode() + " - "
		        + conceptReferenceTermMap.getTermB().getConceptSource().getName() + ": "
		        + conceptReferenceTermMap.getTermB().getCode();
	}
	
	public SimpleObject getAuditInfo(ConceptReferenceTermMap delegate) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(delegate, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(delegate.getDateCreated(), Representation.DEFAULT));
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(delegate, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(delegate.getDateChanged(), Representation.DEFAULT));
		return ret;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public ConceptReferenceTermMap newDelegate() {
		return new ConceptReferenceTermMap();
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public ConceptReferenceTermMap save(ConceptReferenceTermMap conceptReferenceTermMap) {
		ConceptReferenceTerm termA = conceptReferenceTermMap.getTermA();
		termA.addConceptReferenceTermMap(conceptReferenceTermMap);
		
		Context.getConceptService().saveConceptReferenceTerm(termA);
		
		return conceptReferenceTermMap;
	}
	
	/**
	 * Fetches a conceptReferenceTerm by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public ConceptReferenceTermMap getByUniqueId(String uuid) {
		return Context.getService(RestHelperService.class).getObjectByUuid(ConceptReferenceTermMap.class, uuid);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(ConceptReferenceTermMap conceptReferenceTermMap, RequestContext context) throws ResponseException {
		if (conceptReferenceTermMap == null)
			return;
		ConceptReferenceTerm termA = conceptReferenceTermMap.getTermA();
		termA.removeConceptReferenceTermMap(conceptReferenceTermMap);
		
		Context.getConceptService().saveConceptReferenceTerm(termA);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<ConceptReferenceTermMap> doGetAll(RequestContext context) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getResourceVersion()
	 */
	@Override
	public String getResourceVersion() {
		return "1.9";
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(ConceptReferenceTermMap delegate, String reason, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
}

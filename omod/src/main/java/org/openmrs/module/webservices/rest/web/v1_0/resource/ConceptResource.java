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
package org.openmrs.module.webservices.rest.web.v1_0.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.annotation.Handler;
import org.openmrs.api.APIException;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.RepHandler;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.MetadataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ConversionException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.util.LocaleUtility;

/**
 * {@link Resource} for {@link Concept}, supporting standard CRUD operations
 */
@Resource("concept")
@Handler(supports = Concept.class, order = 0)
public class ConceptResource extends DelegatingCrudResource<Concept> {
	
	@RepHandler(RefRepresentation.class)
	public SimpleObject asRef(Concept delegate) throws ConversionException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("display", "displayString", Representation.DEFAULT);
		if (delegate.isRetired()) {
			description.addProperty("retired");
		}
		description.addSelfLink();
		return convertDelegateToRepresentation(delegate, description);
	}
	
	/**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayName"));
			description.addProperty("name", Representation.DEFAULT);
			description.addProperty("datatype", Representation.REF);
			description.addProperty("conceptClass", Representation.REF);
			description.addProperty("set");
			description.addProperty("version");
			description.addProperty("retired");
			
			description.addProperty("names", Representation.REF);
			description.addProperty("descriptions", Representation.REF);
			
			description.addProperty("answers", Representation.REF);
			description.addProperty("setMembers", Representation.REF);
			//description.addProperty("conceptMappings", Representation.REF);  add as subresource
			
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display", findMethod("getDisplayName"));
			description.addProperty("name", Representation.DEFAULT);
			description.addProperty("datatype", Representation.DEFAULT);
			description.addProperty("conceptClass", Representation.DEFAULT);
			description.addProperty("set");
			description.addProperty("version");
			description.addProperty("retired");
			
			description.addProperty("names", Representation.DEFAULT);
			description.addProperty("descriptions", Representation.DEFAULT);
			
			description.addProperty("answers", Representation.DEFAULT);
			description.addProperty("setMembers", Representation.DEFAULT);
			//description.addProperty("conceptMappings", Representation.DEFAULT);  add as subresource
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * Sets the name property to be the fully specified name of the Concept in the current locale
	 * 
	 * @param instance
	 * @param name
	 */
	@PropertySetter("name")
	public static void setFullySpecifiedName(Concept instance, String name) {
		ConceptName fullySpecifiedName = new ConceptName(name, Context.getLocale());
		instance.setFullySpecifiedName(fullySpecifiedName);
	}
	
	/**
	 * Gets the display name of the Concept delegate
	 * 
	 * @param instance the delegate instance to get the display name off
	 */
	public String getDisplayName(Concept instance) {
		ConceptName cn = instance.getName();
		return cn == null ? null : cn.getName();
	}
	
	/**
	 * Must put this here because we cannot extend {@link MetadataDelegatingCrudResource}
	 * 
	 * @param concept the delegate concept
	 * @return audit information
	 * @throws Exception
	 */
	public SimpleObject getAuditInfo(Concept concept) throws Exception {
		SimpleObject ret = new SimpleObject();
		ret.put("creator", ConversionUtil.getPropertyWithRepresentation(concept, "creator", Representation.REF));
		ret.put("dateCreated", ConversionUtil.convertToRepresentation(concept.getDateCreated(), Representation.DEFAULT));
		if (concept.isRetired()) {
			ret.put("retiredBy", ConversionUtil.getPropertyWithRepresentation(concept, "retiredBy", Representation.REF));
			ret.put("dateRetired", ConversionUtil.convertToRepresentation(concept.getDateRetired(), Representation.DEFAULT));
			ret.put("retireReason", ConversionUtil
			        .convertToRepresentation(concept.getRetireReason(), Representation.DEFAULT));
		}
		ret.put("changedBy", ConversionUtil.getPropertyWithRepresentation(concept, "changedBy", Representation.REF));
		ret.put("dateChanged", ConversionUtil.convertToRepresentation(concept.getDateChanged(), Representation.DEFAULT));
		return ret;
	}
	
	/**
	 * @see DelegatingCrudResource#newDelegate()
	 */
	@Override
	public Concept newDelegate() {
		return new Concept();
	}
	
	/**
	 * @see DelegatingCrudResource#save(java.lang.Object)
	 */
	@Override
	public Concept save(Concept c) {
		return Context.getConceptService().saveConcept(c);
	}
	
	/**
	 * Fetches a concept by uuid, if no match is found, it tries to look up one with a matching name
	 * with the assumption that the passed parameter is a concept name
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Concept getByUniqueId(String uuidOrName) {
		Concept concept = Context.getConceptService().getConceptByUuid(uuidOrName);
		
		if (concept == null) {
			//We assume the caller was fetching by name if no concept was after looking up by uuid
			// NOT using getConcept here because that also searches on conceptId
			concept = Context.getConceptService().getConceptByName(uuidOrName);
			if (concept != null) {
				boolean isPreferredOrFullySpecified = false;
				for (ConceptName name : concept.getNames()) {
					if (name.getName().equalsIgnoreCase(uuidOrName) && (name.isPreferred() || name.isFullySpecifiedName())) {
						isPreferredOrFullySpecified = true;
						break;
					}
				}
				if (!isPreferredOrFullySpecified)
					throw new APIException("The concept name should be either a fully specified or locale preferred name");
			}
		}
		
		return concept;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(Concept concept, RequestContext context) throws ResponseException {
		if (concept == null)
			return;
		Context.getConceptService().purgeConcept(concept);
	}
	
	/**
	 * This does not include retired concepts
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doGetAll(org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected NeedsPaging<Concept> doGetAll(RequestContext context) {
		return new NeedsPaging<Concept>(Context.getConceptService().getAllConcepts(null, true, false), context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected AlreadyPaged<Concept> doSearch(String query, RequestContext context) {
		ConceptService service = Context.getConceptService();
		
		List<ConceptSearchResult> searchResults;
		
		// get the user's locales...and then convert that from a set to a list
		List<Locale> locales = new ArrayList<Locale>(LocaleUtility.getLocalesInOrder());
		
		searchResults = service.getConcepts(query, locales, false, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
		    Collections.EMPTY_LIST, Collections.EMPTY_LIST, null, context.getStartIndex(), context.getLimit());
		
		// convert search results into list of concepts
		List<Concept> results = new ArrayList<Concept>(searchResults.size());
		for (ConceptSearchResult csr : searchResults) {
			results.add(csr.getConcept());
		}
		
		Integer count = service.getCountOfConcepts(query, locales, false, Collections.EMPTY_LIST, Collections.EMPTY_LIST,
		    Collections.EMPTY_LIST, Collections.EMPTY_LIST, null);
		boolean hasMore = count > context.getStartIndex() + context.getLimit();
		
		return new AlreadyPaged<Concept>(context, results, hasMore);
	}
	
	@Override
	protected void delete(Concept c, String reason, RequestContext context) throws ResponseException {
		if (c.isRetired()) {
			// since DELETE should be idempotent, we return success here
			return;
		}
		Context.getConceptService().retireConcept(c, reason);
	}
	
	/**
	 * @param instance
	 * @return the list of Concepts or Drugs
	 */
	@PropertyGetter("answers")
	public static Object getAnswers(Concept instance) {
		List<Object> answers = new ArrayList<Object>();
		Set<ConceptAnswer> conceptAnswers = new TreeSet<ConceptAnswer>();
		conceptAnswers.addAll(instance.getAnswers());
		
		for (ConceptAnswer conceptAnswer : conceptAnswers) {
			if (conceptAnswer.getAnswerDrug() != null) {
				answers.add(conceptAnswer.getAnswerDrug());
			} else if (conceptAnswer.getAnswerConcept() != null) {
				answers.add(conceptAnswer.getAnswerConcept());
			}
		}
		
		return answers;
	}
	
	/**
	 * @param instance
	 * @param answers the list of Concepts or Drugs
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("answers")
	public static void setAnswers(Concept instance, List<Object> answers) throws ResourceDoesNotSupportOperationException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @param instance
	 * @param setMembers the list of Concepts
	 */
	@PropertySetter("setMembers")
	public static void setSetMembers(Concept instance, List<Concept> setMembers) {
		instance.getConceptSets().clear();
		
		if (setMembers == null || setMembers.isEmpty()) {
			instance.setSet(false);
		} else {
			instance.setSet(true);
			
			for (Concept setMember : setMembers) {
				instance.addSetMember(setMember);
			}
		}
	}
}

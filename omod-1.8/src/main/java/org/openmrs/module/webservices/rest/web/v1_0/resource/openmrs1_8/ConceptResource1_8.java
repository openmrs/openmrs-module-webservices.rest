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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.openmrs.Concept;
import org.openmrs.ConceptAnswer;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptDescription;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.Drug;
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
import org.openmrs.module.webservices.rest.web.representation.NamedRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
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
@Resource(name = RestConstants.VERSION_1 + "/concept", order = 2, supportedClass = Concept.class, supportedOpenmrsVersions = "1.8.*")
public class ConceptResource1_8 extends DelegatingCrudResource<Concept> {
	
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
	
	@RepHandler(FullRepresentation.class)
	public SimpleObject asFull(Concept delegate) throws ConversionException {
        DelegatingResourceDescription description = fullRepresentationDescription(delegate);
		return convertDelegateToRepresentation(delegate, description);
	}

    @RepHandler(value = NamedRepresentation.class, name = "fullchildren")
    public SimpleObject asFullChildren(Concept delegate) throws ConversionException {
		Set<String> path = new HashSet<String>();
		path.add(delegate.getUuid());
		assertNoCycles(delegate, path);

        return asFullChildrenInternal(delegate);
    }

	protected void assertNoCycles(Concept delegate, Set<String> path) throws ConversionException {
		for (Concept member: delegate.getSetMembers()) {
			if (path.add(member.getUuid())) {
				assertNoCycles(member, path);
			} else {
				throw new ConversionException("Cycles in children are not supported. Concept with uuid " + delegate.getUuid() + " repeats in a set.");
			}
			path.remove(member.getUuid());
		}
	}

	/**
	 * It is used internally for the fullchildren representation. Contrary to the fullchildren handler it does not check for cycles.
	 *
	 * @param delegate
	 * @return
	 * @throws ConversionException
	 */
	@RepHandler(value = NamedRepresentation.class, name = "fullchildreninternal")
	public SimpleObject asFullChildrenInternal(Concept delegate) throws ConversionException {
		DelegatingResourceDescription description = fullRepresentationDescription(delegate);
		description.removeProperty("setMembers");
		description.addProperty("setMembers", new NamedRepresentation("fullchildreninternal"));
		description.removeProperty("answers");
		description.addProperty("answers", Representation.FULL);
		return convertDelegateToRepresentation(delegate, description);
	}

	@Override
    public List<Representation> getAvailableRepresentations() {
        List<Representation> availableRepresentations = super.getAvailableRepresentations();
        availableRepresentations.add(new NamedRepresentation("fullchildren"));
        return availableRepresentations;
    }

    protected DelegatingResourceDescription fullRepresentationDescription(Concept delegate) {
        DelegatingResourceDescription description = new DelegatingResourceDescription();
        description.addProperty("uuid");
        description.addProperty("display");
        description.addProperty("name", Representation.DEFAULT);
        description.addProperty("datatype", Representation.DEFAULT);
        description.addProperty("conceptClass", Representation.DEFAULT);
        description.addProperty("set");
        description.addProperty("version");
        description.addProperty("retired");

        description.addProperty("names", Representation.DEFAULT);
        description.addProperty("descriptions", Representation.DEFAULT);

        description.addProperty("mappings", Representation.DEFAULT);

        description.addProperty("answers", Representation.DEFAULT);
        description.addProperty("setMembers", Representation.DEFAULT);
        //description.addProperty("conceptMappings", Representation.DEFAULT);  add as subresource
        description.addProperty("auditInfo", findMethod("getAuditInfo"));
        description.addSelfLink();
        if (delegate.isNumeric()) {
            description.addProperty("hiNormal");
            description.addProperty("hiAbsolute");
            description.addProperty("hiCritical");
            description.addProperty("lowNormal");
            description.addProperty("lowAbsolute");
            description.addProperty("lowCritical");
            description.addProperty("units");
            description.addProperty("precise");
        }
        return description;
    }

    /**
	 * @see DelegatingCrudResource#getRepresentationDescription(Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("display");
			description.addProperty("name", Representation.DEFAULT);
			description.addProperty("datatype", Representation.REF);
			description.addProperty("conceptClass", Representation.REF);
			description.addProperty("set");
			description.addProperty("version");
			description.addProperty("retired");
			
			description.addProperty("names", Representation.REF);
			description.addProperty("descriptions", Representation.REF);
			
			description.addProperty("mappings", Representation.REF);
			
			description.addProperty("answers", Representation.REF);
			description.addProperty("setMembers", Representation.REF);
			//description.addProperty("conceptMappings", Representation.REF);  add as subresource
			
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
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
		description.addRequiredProperty("names");
		description.addRequiredProperty("datatype");
		description.addRequiredProperty("conceptClass");
		
		description.addProperty("descriptions");
		description.addProperty("set");
		description.addProperty("version");
		description.addProperty("mappings");
		description.addProperty("answers");
		description.addProperty("setMembers");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = super.getUpdatableProperties();
		
		description.addProperty("name");
		
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("names", "descriptions", "conceptMappings");
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
	 * It's needed, because of ConversionException: Don't know how to handle collection class:
	 * interface java.util.Collection
	 * 
	 * @param instance
	 * @param names
	 */
	@PropertySetter("names")
	public static void setNames(Concept instance, List<ConceptName> names) {
		instance.setNames(new HashSet<ConceptName>(names));
	}
	
	/**
	 * It's needed, because of ConversionException: Don't know how to handle collection class:
	 * interface java.util.Collection
	 * 
	 * @param instance
	 * @param descriptions
	 */
	@PropertySetter("descriptions")
	public static void setDescriptions(Concept instance, List<ConceptDescription> descriptions) {
		instance.setDescriptions(new HashSet<ConceptDescription>(descriptions));
	}
	
	/**
	 * It's needed, because of ConversionException: Don't know how to handle collection class:
	 * interface java.util.Collection
	 * 
	 * @param instance
	 * @param mappings
	 */
	@PropertySetter("mappings")
	public static void setMappings(Concept instance, List<ConceptMap> mappings) {
		instance.setConceptMappings(mappings);
	}
	
	@PropertyGetter("mappings")
	public static List<ConceptMap> getMappings(Concept instance) {
		return new ArrayList<ConceptMap>(instance.getConceptMappings());
	}
	
	/**
	 * Gets the display name of the Concept delegate
	 * 
	 * @param instance the delegate instance to get the display name off
	 */
	@PropertyGetter("display")
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
	 * Fetches a concept by uuid
	 * 
	 * @see DelegatingCrudResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public Concept getByUniqueId(String uuid) {
		return Context.getConceptService().getConceptByUuid(uuid);
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
		List<Concept> allConcepts = Context.getConceptService().getAllConcepts(null, true, context.getIncludeAll());
		return new NeedsPaging<Concept>(allConcepts, context);
	}
	
	/**
	 * Concept searches support the following additional query parameters:
	 * <ul>
	 * <li>answerTo=(uuid): restricts results to concepts that are answers to the given concept uuid
	 * </li>
	 * <li>memberOf=(uuid): restricts to concepts that are set members of the given concept set's
	 * uuid</li>
	 * </ul>
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource#doSearch(RequestContext)
	 */
	@Override
	protected PageableResult doSearch(RequestContext context) {
		ConceptService service = Context.getConceptService();
		Integer startIndex = null;
		Integer limit = null;
		boolean canPage = true;
		
		// Collect information for answerTo and memberOf query parameters
		String answerToUuid = context.getRequest().getParameter("answerTo");
		String memberOfUuid = context.getRequest().getParameter("memberOf");
		Concept answerTo = null;
		List<Concept> memberOfList = null;
		if (StringUtils.isNotBlank(answerToUuid)) {
			try {
				answerTo = (Concept) ConversionUtil.convert(answerToUuid, Concept.class);
			}
			catch (ConversionException ex) {
				log.error("Unexpected exception while retrieving answerTo Concept with UUID " + answerToUuid, ex);
			}
		}
		
		if (StringUtils.isNotBlank(memberOfUuid)) {
			Concept memberOf = service.getConceptByUuid(memberOfUuid);
			memberOfList = service.getConceptsByConceptSet(memberOf);
			canPage = false; // ConceptService does not support memberOf searches, so paging must be deferred.
		}
		
		// Only set startIndex and limit if we can return paged results
		if (canPage) {
			startIndex = context.getStartIndex();
			limit = context.getLimit();
		}
		
		List<ConceptSearchResult> searchResults;
		
		// get the user's locales...and then convert that from a set to a list
		List<Locale> locales = new ArrayList<Locale>(LocaleUtility.getLocalesInOrder());
		
		searchResults = service.getConcepts(context.getParameter("q"), locales, context.getIncludeAll(), null, null, null,
		    null, answerTo, startIndex, limit);
		
		// convert search results into list of concepts
		List<Concept> results = new ArrayList<Concept>(searchResults.size());
		for (ConceptSearchResult csr : searchResults) {
			// apply memberOf filter
			if (memberOfList == null || memberOfList.contains(csr.getConcept()))
				results.add(csr.getConcept());
		}
		
		PageableResult result = null;
		if (canPage) {
			Integer count = service.getCountOfConcepts(context.getParameter("q"), locales, false, Collections
			        .<ConceptClass> emptyList(), Collections.<ConceptClass> emptyList(), Collections
			        .<ConceptDatatype> emptyList(), Collections.<ConceptDatatype> emptyList(), answerTo);
			boolean hasMore = count > startIndex + limit;
			result = new AlreadyPaged<Concept>(context, results, hasMore);
		} else {
			result = new NeedsPaging<Concept>(results, context);
		}
		
		return result;
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
		List<ConceptAnswer> conceptAnswers = new ArrayList<ConceptAnswer>();
		conceptAnswers.addAll(instance.getAnswers(false));
		Collections.sort(conceptAnswers);
		
		List<Object> answers = new ArrayList<Object>();
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
	 * @param answerUuids the list of Concepts or Drugs
	 * @throws ResourceDoesNotSupportOperationException
	 */
	@PropertySetter("answers")
	public static void setAnswers(Concept instance, List<String> answerUuids /*Concept or Drug uuid*/)
	        throws ResourceDoesNotSupportOperationException {
		
		// remove answers that are not in the new list
		Iterator<ConceptAnswer> iterator = instance.getAnswers(false).iterator();
		while (iterator.hasNext()) {
			ConceptAnswer answer = iterator.next();
			String conceptUuid = answer.getConcept().getUuid();
			String drugUuid = (answer.getAnswerDrug() != null) ? answer.getAnswerDrug().getUuid() : null;
			if (answerUuids.contains(conceptUuid)) {
				answerUuids.remove(conceptUuid); // remove from passed in list
			} else if (answerUuids.contains(drugUuid)) {
				answerUuids.remove(drugUuid); // remove from passed in list
			} else
				instance.removeAnswer(answer); // remove from concept question object
		}
		
		List<Object> answerObjects = new ArrayList<Object>(answerUuids.size());
		for (String uuid : answerUuids) {
			Concept c = Context.getConceptService().getConceptByUuid(uuid);
			if (c != null) {
				answerObjects.add(c);
			} else {
				// it is a drug
				Drug drug = Context.getConceptService().getDrugByUuid(uuid);
				if (drug != null)
					answerObjects.add(drug);
				else
					throw new ResourceDoesNotSupportOperationException("There is no concept or drug with given uuid: "
					        + uuid);
			}
		}
		
		// add in new answers
		for (Object obj : answerObjects) {
			ConceptAnswer answerToAdd = null;
			if (obj.getClass().isAssignableFrom(Concept.class))
				answerToAdd = new ConceptAnswer((Concept) obj);
			else
				answerToAdd = new ConceptAnswer(((Drug) obj).getConcept(), (Drug) obj);
			
			answerToAdd.setCreator(Context.getAuthenticatedUser());
			answerToAdd.setDateCreated(new Date());
			instance.addAnswer(answerToAdd);
		}
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

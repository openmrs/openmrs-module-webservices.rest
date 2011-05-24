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
package org.openmrs.module.webservices.rest.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.CohortMember;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.api.RestService;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * Sub-resource for cohort members
 */
@SubResource(parent = CohortResource.class, path = "members")
@Handler(supports = CohortMember.class, order = 0)
public class CohortMemberResource extends DelegatingSubResource<CohortMember, Cohort, CohortResource> {
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public List<CohortMember> doGetAll(Cohort parent, RequestContext context) throws ResponseException {
		List<CohortMember> members = new ArrayList<CohortMember>();
		for (Patient cohortMember : Context.getPatientSetService().getPatients(parent.getMemberIds())) {
			members.add(new CohortMember(cohortMember, parent));
		}
		return members;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Cohort getParent(CohortMember instance) {
		return instance.getCohort();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(CohortMember instance, Cohort parent) {
		instance.setCohort(parent);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(CohortMember delegate, String reason, RequestContext context) throws ResponseException {
		removeMemberFromCohort(delegate);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public CohortMember getByUniqueId(String uniqueId) {
		return new CohortMember(Context.getPatientService().getPatientByUuid(uniqueId), null);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("patient");
			description.addProperty("uri", findMethod("getUri"));
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("patient");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addProperty("uri", findMethod("getUri"));
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	protected CohortMember newDelegate() {
		return new CohortMember();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(CohortMember delegate, RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	/**
	 * @should add patient to cohort
	 */
	@Override
	protected CohortMember save(CohortMember delegate) {
		addMemberToCohort(delegate);
		return delegate;
	}
	
	/**
	 * @param member the patient to be added to cohort
	 */
	public void addMemberToCohort(CohortMember member) {
		getParent(member).addMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}
	
	/**
	 * @param member the patient to be removed from cohort
	 */
	public void removeMemberFromCohort(CohortMember member) {
		getParent(member).removeMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#create(java.lang.String,
	 *      org.openmrs.module.webservices.rest.SimpleObject,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
		Cohort parent = Context.getCohortService().getCohortByUuid(parentUniqueId);
		CohortMember delegate = newDelegate();
		setParent(delegate, parent);
		delegate.setPatient(Context.getPatientService().getPatientByUuid(post.get("patient").toString()));
		delegate = save(delegate);
		return ConversionUtil.convertToRepresentation(delegate, Representation.DEFAULT);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#delete(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException {
		CohortMember delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		Cohort parent = Context.getCohortService().getCohortByUuid(parentUniqueId);
		setParent(delegate, parent);
		delete(delegate, reason, context);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#retrieve(java.lang.String,
	 *      java.lang.String, org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public Object retrieve(String parentUniqueId, String uuid, RequestContext context) throws ResponseException {
		CohortMember delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		delegate.setCohort(Context.getCohortService().getCohortByUuid(parentUniqueId));
		return asRepresentation(delegate, context.getRepresentation());
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getUri(java.lang.Object)
	 */
	@Override
	public String getUri(Object instance) {
		org.openmrs.module.webservices.rest.web.annotation.SubResource sub = getClass().getAnnotation(
		    org.openmrs.module.webservices.rest.web.annotation.SubResource.class);
		@SuppressWarnings("unchecked")
		CohortMember instanceAsT = (CohortMember) instance;
		String parentUri = Context.getService(RestService.class).getResource(sub.parent()).getUri(getParent(instanceAsT));
		return parentUri + "/" + sub.path() + "/" + instanceAsT.getPatient().getUuid();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getAll(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public List<Object> getAll(String parentUniqueId, RequestContext context) throws ResponseException {
		Cohort parent = Context.getCohortService().getCohortByUuid(parentUniqueId);
		List<Object> ret = new ArrayList<Object>();
		Representation rep = Representation.FULL;
		if (context.getRepresentation().equals(Representation.DEFAULT))
			rep = Representation.REF;
		else if (context.getRepresentation().equals(Representation.FULL))
			rep = Representation.DEFAULT;
		for (CohortMember member : doGetAll(parent, context))
			ret.add(asRepresentation(member, rep));
		return ret;
	}
	
	/**
	 * @param member
	 * @return cohortname + patient fullname (for concise display purposes)
	 */
	public String getDisplayString(CohortMember member) {
		org.openmrs.module.webservices.rest.web.annotation.SubResource sub = getClass().getAnnotation(
		    org.openmrs.module.webservices.rest.web.annotation.SubResource.class);
		return ((PatientResource) Context.getService(RestService.class).getResource(sub.parent())).getDisplayString(member
		        .getPatient());
	}
}

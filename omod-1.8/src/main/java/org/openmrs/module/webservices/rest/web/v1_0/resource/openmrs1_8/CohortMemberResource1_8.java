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

import org.openmrs.Cohort;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.ConversionUtil;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertyGetter;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ObjectNotFoundException;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.wrapper.openmrs1_8.CohortMember1_8;

import java.util.ArrayList;
import java.util.List;

/**
 * Sub-resource for cohort members
 */
@SubResource(parent = CohortResource1_8.class, path = "member", supportedClass = CohortMember1_8.class, supportedOpenmrsVersions = {
		"1.9.*", "1.10.*", "1.11.*", "1.12.*" }, order = 200)
public class CohortMemberResource1_8 extends
		DelegatingSubResource<CohortMember1_8, Cohort, CohortResource1_8> {

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public NeedsPaging<CohortMember1_8> doGetAll(Cohort parent,
			RequestContext context) throws ResponseException {
		List<CohortMember1_8> members = new ArrayList<CohortMember1_8>();
		for (Patient cohortMember : Context.getPatientSetService().getPatients(
				parent.getMemberIds())) {
			members.add(new CohortMember1_8(cohortMember, parent));
		}
		return new NeedsPaging<CohortMember1_8>(members, context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Cohort getParent(CohortMember1_8 instance) {
		return instance.getCohort();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(CohortMember1_8 instance, Cohort parent) {
		instance.setCohort(parent);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#delete(java.lang.Object,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	protected void delete(CohortMember1_8 delegate, String reason,
			RequestContext context) throws ResponseException {
		removeMemberFromCohort(delegate);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getByUniqueId(java.lang.String)
	 */
	@Override
	public CohortMember1_8 getByUniqueId(String uniqueId) {
		return new CohortMember1_8(Context.getPatientService()
				.getPatientByUuid(uniqueId), null);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getRepresentationDescription(org.openmrs.module.webservices.rest.web.representation.Representation)
	 */
	@Override
	public DelegatingResourceDescription getRepresentationDescription(
			Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("patient");
			description.addSelfLink();
			description.addLink("full", ".?v="
					+ RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("display");
			description.addProperty("patient");
			// description.addProperty("auditInfo", findMethod("getAuditInfo"));
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
		description.addRequiredProperty("patient");
		return description;
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUpdatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getUpdatableProperties() {
		return getCreatableProperties();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#newDelegate()
	 */
	@Override
	public CohortMember1_8 newDelegate() {
		return new CohortMember1_8();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#purge(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void purge(CohortMember1_8 delegate, RequestContext context)
			throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}

	/**
	 * @should add patient to cohort
	 */
	@Override
	public CohortMember1_8 save(CohortMember1_8 delegate) {
		addMemberToCohort(delegate);
		return delegate;
	}

	/**
	 * @param member
	 *            the patient to be added to cohort
	 */
	public void addMemberToCohort(CohortMember1_8 member) {
		getParent(member).addMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}

	/**
	 * @param member
	 *            the patient to be removed from cohort
	 */
	public void removeMemberFromCohort(CohortMember1_8 member) {
		getParent(member).removeMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#create(java.lang.String,
	 *      org.openmrs.module.webservices.rest.SimpleObject,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public Object create(String parentUniqueId, SimpleObject post,
			RequestContext context) throws ResponseException {
		Cohort parent = Context.getCohortService().getCohortByUuid(
				parentUniqueId);
		CohortMember1_8 delegate = newDelegate();
		setParent(delegate, parent);
		delegate.setPatient(Context.getPatientService().getPatientByUuid(
				post.get("patient").toString()));
		delegate = save(delegate);
		return ConversionUtil.convertToRepresentation(delegate,
				Representation.DEFAULT);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#delete(java.lang.String,
	 *      java.lang.String, java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public void delete(String parentUniqueId, String uuid, String reason,
			RequestContext context) throws ResponseException {
		CohortMember1_8 delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		Cohort parent = Context.getCohortService().getCohortByUuid(
				parentUniqueId);
		setParent(delegate, parent);
		delete(delegate, reason, context);
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#retrieve(java.lang.String,
	 *      java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public Object retrieve(String parentUniqueId, String uuid,
			RequestContext context) throws ResponseException {
		CohortMember1_8 delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		delegate.setCohort(Context.getCohortService().getCohortByUuid(
				parentUniqueId));
		return asRepresentation(delegate, context.getRepresentation());
	}

	/**
	 * Overridden here since the unique id is not on CohortMember directly
	 * 
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getUniqueId(java.lang.Object)
	 */
	@Override
	protected String getUniqueId(CohortMember1_8 delegate) {
		return delegate.getPatient().getUuid();
	}

	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getAll(java.lang.String,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	/*
	 * TODO: Should be deleted since doGetAll is already overridden
	 * 
	 * @Override public List<Object> getAll(String parentUniqueId,
	 * RequestContext context) throws ResponseException { Cohort parent =
	 * Context.getCohortService().getCohortByUuid(parentUniqueId); List<Object>
	 * ret = new ArrayList<Object>(); Representation rep = Representation.FULL;
	 * if (context.getRepresentation().equals(Representation.DEFAULT)) rep =
	 * Representation.REF; else if
	 * (context.getRepresentation().equals(Representation.FULL)) rep =
	 * Representation.DEFAULT; for (CohortMember member : doGetAll(parent,
	 * context)) ret.add(asRepresentation(member, rep)); return ret; }
	 */

	/**
	 * @param member
	 *            the patient
	 * @return string that contains cohort member's identifier and full name
	 */
	@PropertyGetter("display")
	public String getDisplayString(CohortMember1_8 member) {

		if (member.getPatient().getPatientIdentifier() == null)
			return "";

		return member.getPatient().getPatientIdentifier().getIdentifier()
				+ " - " + member.getPatient().getPersonName().getFullName();
	}
}

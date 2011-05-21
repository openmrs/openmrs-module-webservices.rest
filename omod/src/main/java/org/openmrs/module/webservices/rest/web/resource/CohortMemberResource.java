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
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
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
@SubResource(parent = CohortResource.class, parentProperty = "REMOVE-THIS-PROPERTY", path = "patients")
@Handler(supports = CohortMember.class, order = 0)
public class CohortMemberResource extends DelegatingSubResource<CohortMember, Cohort, CohortResource> {
	
	@Override
	public List<CohortMember> doGetAll(Cohort parent, RequestContext context) throws ResponseException {
		List<CohortMember> members = new ArrayList<CohortMember>();
		for (Patient cohortMember : Context.getPatientSetService().getPatients(parent.getMemberIds())) {
			members.add(new CohortMember(cohortMember, parent.getUuid()));
		}
		return members;
	}
	
	@Override
	public Cohort getParent(CohortMember instance) {
		return Context.getCohortService().getCohortByUuid(instance.getCohortUuid());
	}
	
	@Override
	public void setParent(CohortMember instance, Cohort parent) {
		instance.setCohortUuid(parent.getUuid());
	}
	
	@Override
	protected void delete(CohortMember delegate, String reason, RequestContext context) throws ResponseException {
		removeMemberFromCohort(delegate);
	}
	
	@Override
	public CohortMember getByUniqueId(String uniqueId) {
		return new CohortMember(Context.getPatientService().getPatientByUuid(uniqueId), null);
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof RefRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("uri", findMethod("getUri"));
			description.addProperty("display", findMethod("getDisplayString"));
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("cohortUuid");
			description.addProperty("patient", Representation.REF);
			description.addProperty("uri", findMethod("getUri"));
			return description;
		}
		return null;
	}
	
	@Override
	protected CohortMember newDelegate() {
		return new CohortMember();
	}
	
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
	
	public void addMemberToCohort(CohortMember member) {
		getParent(member).addMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}
	
	public void removeMemberFromCohort(CohortMember member) {
		getParent(member).removeMember(member.getPatient().getId());
		Context.getCohortService().saveCohort(getParent(member));
	}
	
	@Override
	public Object create(String parentUniqueId, SimpleObject post, RequestContext context) throws ResponseException {
		Cohort parent = Context.getCohortService().getCohortByUuid(parentUniqueId);
		CohortMember delegate = newDelegate();
		setParent(delegate, parent);
		delegate.setPatient(Context.getPatientService().getPatientByUuid(post.get("patientUuid").toString()));
		delegate.setCohortUuid(parentUniqueId);
		delegate = save(delegate);
		return ConversionUtil.convertToRepresentation(delegate, Representation.DEFAULT);
	}
	
	@Override
	public void delete(String parentUniqueId, String uuid, String reason, RequestContext context) throws ResponseException {
		CohortMember delegate = getByUniqueId(uuid);
		if (delegate == null)
			throw new ObjectNotFoundException();
		Cohort parent = Context.getCohortService().getCohortByUuid(parentUniqueId);
		setParent(delegate, parent);
		delete(delegate, reason, context);
	}
	
	@Override
	public Object retrieve(String parentUniqueId, String uuid, RequestContext context) throws ResponseException {
		CohortMember delegate = getByUniqueId(uuid);
		delegate.setCohortUuid(parentUniqueId);
		if (delegate == null)
			throw new ObjectNotFoundException();
		return asRepresentation(delegate, context.getRepresentation());
	}
	
}

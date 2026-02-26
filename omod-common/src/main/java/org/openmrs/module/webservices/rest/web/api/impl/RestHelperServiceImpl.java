/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.webservices.rest.web.api.impl;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.hibernate.Session;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openmrs.api.context.Context.getRegisteredComponents;

/**
 * REST helper service, which must not be used outside of the REST module.
 */
public class RestHelperServiceImpl extends BaseOpenmrsService implements RestHelperService {
	
	DbSessionFactory sessionFactory;
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	private Session getHibernateSession() {
		return sessionFactory.getHibernateSessionFactory().getCurrentSession();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectByUuid(Class,
	 *      String)
	 */
	@Override
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public <T> T getObjectByUuid(Class<? extends T> type, String uuid) {
		Session session = getHibernateSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery((Class<T>) type);
		Root<T> root = cq.from((Class<T>) type);
		cq.where(cb.equal(root.get("uuid"), uuid));
		List<T> results = session.createQuery(cq).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}
	
	@Override
	public <T> T getObjectById(Class<? extends T> type, Serializable id) {
		return type.cast(getHibernateSession().get(type, id));
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectsByFields(Class,
	 *      Field...)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getObjectsByFields(Class<? extends T> type, Field... fields) {
		Session session = getHibernateSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery((Class<T>) type);
		Root<T> root = cq.from((Class<T>) type);
		List<Predicate> predicates = new ArrayList<>();
		for (Field field : fields) {
			if (field != null) {
				predicates.add(cb.equal(root.get(field.getName()), field.getValue()));
			}
		}
		if (!predicates.isEmpty()) {
			cq.where(predicates.toArray(new Predicate[0]));
		}
		return session.createQuery(cq).getResultList();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getPatients(Collection)
	 */
	@Override
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		List<Patient> ret = new ArrayList<Patient>();
		
		if (!patientIds.isEmpty()) {
			Session session = getHibernateSession();
			CriteriaBuilder cb = session.getCriteriaBuilder();
			CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
			Root<Patient> root = cq.from(Patient.class);
			cq.where(
				cb.and(
					root.get("patientId").in(patientIds),
					cb.equal(root.get("voided"), false)
				)
			);
			session.setCacheMode(org.hibernate.CacheMode.IGNORE);
			List<Patient> temp = session.createQuery(cq).getResultList();
			for (Patient p : temp) {
				ret.add(p);
			}
		}
		
		return ret;
	}
	
	@Override
	public List<Patient> findPatientsByIdentifierStartingWith(String identifier, boolean includeAll) {
		Session session = getHibernateSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();
		CriteriaQuery<Patient> cq = cb.createQuery(Patient.class);
		Root<Patient> root = cq.from(Patient.class);
		Join<?, ?> identifiers = root.join("identifiers");
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.like(identifiers.get("identifier"), identifier + "%"));
		if (!includeAll) {
			predicates.add(cb.equal(root.get("voided"), false));
		}
		cq.where(predicates.toArray(new Predicate[0]));
		cq.distinct(true);
		return session.createQuery(cq).getResultList();
	}
	
	/**
	 * @see RestHelperService#getRegisteredSearchHandlers()
	 */
	@Override
	public List<SearchHandler> getRegisteredSearchHandlers() {
		final List<SearchHandler> result = Context.getRegisteredComponents(SearchHandler.class);
		return result != null ? result : new ArrayList<SearchHandler>();
	}
	
	/**
	 * @see RestHelperService#getRegisteredRegisteredSubclassHandlers()
	 */
	@Override
	public List<DelegatingSubclassHandler> getRegisteredRegisteredSubclassHandlers() {
		final List<DelegatingSubclassHandler> result = getRegisteredComponents(DelegatingSubclassHandler.class);
		return result != null ? result : new ArrayList<DelegatingSubclassHandler>();
	}
}

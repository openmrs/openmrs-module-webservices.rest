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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.openmrs.Patient;
import org.openmrs.PatientIdentifier;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.api.db.hibernate.HibernateUtil;
import org.openmrs.api.db.hibernate.MatchMode;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.openmrs.api.context.Context.getRegisteredComponents;

/**
 * REST helper service, which must not be used outside of the REST module.
 */
public class RestHelperServiceImpl extends BaseOpenmrsService implements RestHelperService {
	
	DbSessionFactory sessionFactory;
	
	Method method;
	
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectByUuid(Class,
	 *      String)
	 */
	@Override
	@Transactional(readOnly = true)
	public <T> T getObjectByUuid(Class<? extends T> type, String uuid) {
        return HibernateUtil.getUniqueEntityByUUID(sessionFactory.getHibernateSessionFactory(), type, uuid);
	}
	
	private DbSession getSession() {
		if (method == null) {
			try {
				return sessionFactory.getCurrentSession();
			}
			catch (NoSuchMethodError error) {
				//Supports Hibernate 3 by casting org.hibernate.classic.Session to org.hibernate.Session
				try {
					method = sessionFactory.getClass().getMethod("getCurrentSession");
					return (DbSession) method.invoke(sessionFactory);
				}
				catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		} else {
			try {
				return (DbSession) method.invoke(sessionFactory);
			}
			catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectById(Class,
	 *      Serializable)
	 */
	@Override
	public <T> T getObjectById(Class<? extends T> type, Serializable id) {
		return type.cast(getSession().get(type, id));
	}

    /**
     * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectsByFields(Class,
     *      Field...)
     */
    @Override
    public <T> List<T> getObjectsByFields(Class<T> type, Field... fields) {
        Session session = sessionFactory.getHibernateSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(type);
        Root<T> root = query.from(type);

        List<Predicate> predicates = new ArrayList<>();

        for (Field field : fields) {
            if (field != null) {
                predicates.add(cb.equal(root.get(field.getName()), field.getValue()));
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        return session.createQuery(query).getResultList();
    }

    /**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getPatients(Collection)
	 */
	@Override
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		List<Patient> ret = new ArrayList<>();

		if (!patientIds.isEmpty()) {
            Session session = sessionFactory.getHibernateSessionFactory().getCurrentSession();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
            Root<Patient> root = query.from(Patient.class);

            query.select(root)
                 .where(root.get("patientId").in(patientIds),
                        cb.isFalse(root.get("voided")));

			List<Patient> temp = session.createQuery(query)
                    .setCacheMode(CacheMode.IGNORE)
                    .getResultList();

            ret.addAll(temp);
		}
		
		return ret;
	}

    @Override
    public List<Patient> findPatientsByIdentifierStartingWith(String identifier, boolean includeAll) {
        Session session = sessionFactory.getHibernateSessionFactory().getCurrentSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);
        Join<Patient, PatientIdentifier> identifiers = root.join("identifiers");

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.like(
                cb.lower(identifiers.get("identifier")),
                MatchMode.START.toLowerCasePattern(identifier)));

        if (!includeAll) {
            predicates.add(cb.isFalse(root.get("voided")));
        }

        query.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .distinct(true);

        return session.createQuery(query).getResultList();
    }


	/**
	 * @see RestHelperService#getRegisteredSearchHandlers()
	 */
	@Override
	public List<SearchHandler> getRegisteredSearchHandlers() {
		final List<SearchHandler> result = Context.getRegisteredComponents(SearchHandler.class);
		return result != null ? result : new ArrayList<>();
	}
	
	/**
	 * @see RestHelperService#getRegisteredRegisteredSubclassHandlers()
	 */
	@Override
	public List<DelegatingSubclassHandler> getRegisteredRegisteredSubclassHandlers() {
		final List<DelegatingSubclassHandler> result = getRegisteredComponents(DelegatingSubclassHandler.class);
		return result != null ? result : new ArrayList<>();
	}
}

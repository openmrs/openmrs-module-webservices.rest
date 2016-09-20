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
package org.openmrs.module.webservices.rest.web.api.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.openmrs.module.webservices.rest.web.resource.api.SearchHandler;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubclassHandler;
import org.springframework.transaction.annotation.Transactional;

import static org.openmrs.api.context.Context.getRegisteredComponents;

/**
 * REST helper service, which must not be used outside of the REST module.
 */
public class RestHelperServiceImpl extends BaseOpenmrsService implements RestHelperService {
	
	SessionFactory sessionFactory;
	
	Method method;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getObjectByUuid(Class,
	 *      String)
	 */
	@Override
	@Transactional(readOnly = true)
	public <T> T getObjectByUuid(Class<? extends T> type, String uuid) {
		return type.cast(getSession().createCriteria(type).add(Restrictions.eq("uuid", uuid)).uniqueResult());
	}
	
	private Session getSession() {
		if (method == null) {
			try {
				return sessionFactory.getCurrentSession();
			}
			catch (NoSuchMethodError error) {
				//Supports Hibernate 3 by casting org.hibernate.classic.Session to org.hibernate.Session
				try {
					method = sessionFactory.getClass().getMethod("getCurrentSession");
					return (Session) method.invoke(sessionFactory);
				}
				catch (Exception e) {
					throw new IllegalStateException(e);
				}
			}
		} else {
			try {
				return (Session) method.invoke(sessionFactory);
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
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getObjectsByFields(Class<? extends T> type, Field... fields) {
		Criteria criteria = getSession().createCriteria(type);
		for (Field field : fields) {
			if (field != null) {
				criteria.add(Restrictions.eq(field.getName(), field.getValue()));
			}
		}
		return criteria.list();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getPatients(Collection)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<Patient> getPatients(Collection<Integer> patientIds) {
		List<Patient> ret = new ArrayList<Patient>();
		
		if (!patientIds.isEmpty()) {
			Criteria criteria = getSession().createCriteria(Patient.class);
			criteria.setCacheMode(CacheMode.IGNORE);
			criteria.add(Restrictions.in("patientId", patientIds));
			criteria.add(Restrictions.eq("voided", false));
			List<Patient> temp = criteria.list();
			for (Patient p : temp) {
				ret.add(p);
			}
		}
		
		return ret;
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

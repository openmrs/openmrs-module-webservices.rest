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

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.openmrs.ConceptMap;
import org.openmrs.api.impl.BaseOpenmrsService;
import org.openmrs.module.webservices.rest.web.api.RestHelperService;
import org.springframework.transaction.annotation.Transactional;

/**
 * REST helper service, which must not be used outside of the REST module.
 */
public class RestHelperServiceImpl extends BaseOpenmrsService implements RestHelperService {
	
	SessionFactory sessionFactory;
	
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.api.RestHelperService#getConceptMapByUuid(java.lang.String)
	 */
	@Override
    @Transactional(readOnly = true)
	public ConceptMap getConceptMapByUuid(String uuid) {
		return (ConceptMap) sessionFactory.getCurrentSession().createCriteria(ConceptMap.class).add(
		    Restrictions.eq("uuid", uuid)).uniqueResult();
	}
}

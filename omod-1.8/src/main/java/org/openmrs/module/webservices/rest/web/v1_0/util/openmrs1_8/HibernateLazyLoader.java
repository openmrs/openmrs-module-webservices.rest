package org.openmrs.module.webservices.rest.web.v1_0.util.openmrs1_8;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;

public class HibernateLazyLoader {

    public Object load(Object entity) {
        Hibernate.initialize(entity);
        if (entity instanceof HibernateProxy) {
            return ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
        }
        return entity;
    }
}

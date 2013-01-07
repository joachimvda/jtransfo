/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

import org.hibernate.SessionFactory;
import org.jtransfo.ObjectFinder;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Object finder which tries to find the object in the database through Hibernate.
 */
public class HibernateObjectFinder implements ObjectFinder {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public <T> T getObject(Class<T> domainClass, Object to) {
        if (to instanceof IdentifiedTo) {
            Long id = ((IdentifiedTo) to).getId();
            if (null != id) {
                return (T) sessionFactory.getCurrentSession().get(domainClass, id);
            }
        }
        return null;
    }
}

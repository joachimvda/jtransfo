/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.hibernate;

import org.hibernate.proxy.HibernateProxy;
import org.jtransfo.ObjectClassDeterminator;

/**
 * ObjectClassDeterminator which recognizes Hibernate proxies.
 *
 * This allows jTransfo to properly recognize Hibernate proxies without forcing the object to be initialised.
 */
public class HibernateObjectClassDeterminator implements ObjectClassDeterminator {

    @Override
    public Class getObjectClass(Object object) {
        if (object instanceof HibernateProxy) {
            return ((HibernateProxy) object).getHibernateLazyInitializer().getPersistentClass();
        }
        return null;
    }

}

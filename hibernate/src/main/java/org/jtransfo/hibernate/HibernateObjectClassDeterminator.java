/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.hibernate;

import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.jtransfo.ObjectClassDeterminator;

import java.lang.reflect.Field;

/**
 * ObjectClassDeterminator which recognizes Hibernate proxies.
 *
 * This allows jTransfo to properly recognize Hibernate proxies without forcing the object to be initialised.
 */
public class HibernateObjectClassDeterminator implements ObjectClassDeterminator {

    @Override
    public Class getObjectClass(Object object) {
        if (object instanceof HibernateProxy) {
            LazyInitializer lazyInitializer = ((HibernateProxy) object).getHibernateLazyInitializer();
            try {
                // try to avoid initializing all lazy fields by using implementation knowledge
                Field targetField = lazyInitializer.getClass().getDeclaredField("target");
                targetField.setAccessible(true);
                Object target = targetField.get(lazyInitializer);
                if (null != target) {
                    return target.getClass();
                }
            } catch (Exception ex) {
                // ignore
                System.err.println("Cannot get target field for proxy, using default which may be wrong.");
            }
            return lazyInitializer.getImplementation().getClass();
        }
        return null;
    }

}

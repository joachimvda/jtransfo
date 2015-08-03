/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.hibernate;

import org.hibernate.proxy.HibernateProxy;
import org.jtransfo.ObjectReplacer;

/**
 * ObjectClassDeterminator which recognizes Hibernate proxies.
 *
 * This allows jTransfo to properly recognize Hibernate proxies without forcing the object to be initialised.
 */
public class HibernateObjectReplacer implements ObjectReplacer {

    /**
     * Convert an object to something jTransfo should convert.
     * <p>
     * If the object is not replaced, the object itself should be returned.
     * </p>
     *
     * @param object object which may need to be replaced
     * @return replacement object or the object itself
     */
    @Override
    public Object replaceObject(Object object) {
        if (object instanceof HibernateProxy) {
            return ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
        }
        return object;
    }

}

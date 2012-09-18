/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.JTransfoException;

/**
 * Helper for working with transfer objects.
 */
public class ToHelper {

    /**
     * Get domain class for transfer object.
     *
     * @param to transfer object
     * @return domain class as annotated on class
     */
    public Class<?> getDomainClass(Object to) {
        Class<?> toClass = to.getClass();
        DomainClass domainClass = toClass.getAnnotation(DomainClass.class);
        if (null == domainClass) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " not annotated with DomainClass.");
        }
        if (DomainClass.DefaultClass.class != domainClass.domainClass()) {
            return domainClass.domainClass();
        }
        if (DomainClass.DEFAULT_NAME.equals(domainClass.value())) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " DomainClass annotation does not specify class.");
        }
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = ToHelper.class.getClassLoader();
        }
        try {
            return cl.loadClass(domainClass.value());
        } catch (ClassNotFoundException cnfe) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " DomainClass " + domainClass.value() + " not found.", cnfe);
        }
    }

}

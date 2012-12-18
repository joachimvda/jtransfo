/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.JTransfoException;

/**
 * Helper for working with transfer objects.
 */
public class ToHelper {

    private ReflectionHelper reflectionHelper = new ReflectionHelper();

    /**
     * Is the given object a transfer object?
     * <p/>
     * True when there is a {@link DomainClass} annotation on the class.
     *
     * @param object object to test
     * @return true when object is a transfer object
     */
    public boolean isTo(Object object) {
        return isToClass(object.getClass());
    }

    /**
     * Is the given class a transfer object class?
     * <p/>
     * True when there is a {@link DomainClass} annotation on the class.
     *
     * @param toClass object class to test
     * @return true when object is a transfer object
     */
    public boolean isToClass(Class<?> toClass) {
        DomainClass domainClass = toClass.getAnnotation(DomainClass.class);
        return null != domainClass;
    }

    /**
     * Get domain class for transfer object.
     *
     * @param toClass transfer object class
     * @return domain class as annotated on class
     */
    public Class<?> getDomainClass(Class<?> toClass) {
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
        try {
            return reflectionHelper.loadClass(domainClass.value());
        } catch (ClassNotFoundException cnfe) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " DomainClass " + domainClass.value() + " not found.", cnfe);
        }
    }

}

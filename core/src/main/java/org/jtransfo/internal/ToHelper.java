/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.DomainClassDelegate;
import org.jtransfo.JTransfoException;

import java.util.List;

/**
 * Helper for working with transfer objects.
 */
public class ToHelper {

    private ReflectionHelper reflectionHelper = new ReflectionHelper();

    /**
     * Is the given object a transfer object?
     * <p>
     * True when there is a {@link DomainClass} annotation on the class.
     * </p>
     *
     * @param object object to test
     * @return true when object is a transfer object
     */
    public boolean isTo(Object object) {
        return isToClass(object.getClass());
    }

    /**
     * Is the given class a transfer object class?
     * <p>
     * True when there is a {@link DomainClass} annotation on the class.
     * </p>
     *
     * @param toClass object class to test
     * @return true when object is a transfer object
     */
    public boolean isToClass(Class<?> toClass) {
        List<DomainClass> domainClasses = reflectionHelper.getAnnotationWithMeta(toClass, DomainClass.class);
        return 0 != domainClasses.size();
    }

    /**
     * Get domain class for transfer object.
     *
     * @param toClass transfer object class
     * @return domain class as annotated on class
     */
    public Class<?> getDomainClass(Class<?> toClass) {
        List<DomainClass> domainClasses = reflectionHelper.getAnnotationWithMeta(toClass, DomainClass.class);
        if (0 == domainClasses.size()) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " not annotated with DomainClass.");
        }
        if (1 < domainClasses.size()) {
            throw new JTransfoException("Transfer object of type " + toClass.getName() +
                    " DomainClass is ambiguous, check your meta-annotations.");
        }
        DomainClass domainClass = domainClasses.get(0);
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

    /**
     * Get the correct transfer object type for the given domain object.
     * <p>
     * This searches the DomainClassDelegates (if present) to see of there is a better matching transfer object than
     * the one given as parameter.
     * </p>
     *
     * @param toType base transfer object type
     * @param domainObject domain object (instance)
     * @return proper transfer object type to use
     */
    public  Class<?> getToSubType(Class<?> toType, Object domainObject) {
        DomainClassDelegate domainClassDelegate = toType.getAnnotation(DomainClassDelegate.class);
        if (null != domainClassDelegate) {
            for (Class<?> delegate : domainClassDelegate.delegates()) {
                Class<?> delegateDomain = getDomainClass(delegate);
                if (delegateDomain.isInstance(domainObject)) {
                    toType = delegate;
                }
            }
        }
        return toType;
    }

}

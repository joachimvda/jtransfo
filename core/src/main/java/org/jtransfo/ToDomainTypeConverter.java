/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.ToHelper;

/**
 * Recursively use jTransfo to convert fields which are themselves a transfer object.
 */
public class ToDomainTypeConverter implements TypeConverter<Object, Object> {

    private static final String CANNOT_CREATE_INSTANCE_OF = "Cannot create instance of transfer object class ";

    private final ToHelper toHelper;
    private final JTransfo jTransfo;
    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    /**
     * Construct type converter using given ToHelper instance (sharing the reflection cache).
     *
     * @param jTransfo jTransfo engine for conversion
     * @param toHelper TO helper
     */
    public ToDomainTypeConverter(JTransfo jTransfo, ToHelper toHelper) {
        this.jTransfo = jTransfo;
        this.toHelper = toHelper;
    }

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        // TO type should be marked with @DomainClass and domain should match declared
        return toHelper.isToClass(realToType) && realDomainType.isAssignableFrom(toHelper.getDomainClass(realToType));
    }

    @Override
    public Object convert(Object toObject, Class<Object> domainType) throws JTransfoException {
        return jTransfo.convert(toObject);
    }

    @Override
    public Object reverse(Object domainObject, Class<Object> toType) throws JTransfoException {
        try {
            if (null == domainObject) {
                return null;
            }
            return jTransfo.convert(domainObject, reflectionHelper.newInstance(toType));
        } catch (InstantiationException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + toType.getName() + ".",
                    ie);
        } catch (IllegalAccessException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + toType.getName() + ".",
                    ie);
        }
    }
}

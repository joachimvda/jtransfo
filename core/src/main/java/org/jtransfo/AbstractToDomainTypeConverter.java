/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;

/**
 * Recursively use jTransfo to convert fields which are themselves a transfer object.
 */
public abstract class AbstractToDomainTypeConverter implements TypeConverter<Object, Object>, NeedsJTransfo {

    private static final String CANNOT_CREATE_INSTANCE_OF = "Cannot create instance of transfer object class ";

    private JTransfo jTransfo;
    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    @Override
    public void setJTransfo(JTransfo jTransfo) {
        this.jTransfo = jTransfo;
    }

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        // TO type should be marked with @DomainClass and domain should match declared
        return jTransfo.isToClass(realToType) && realDomainType.isAssignableFrom(jTransfo.getDomainClass(realToType));
    }

    /**
     * Do the actual conversion.
     *
     * @param jTransfo jTransfo instance in use
     * @param toObject transfer object
     * @param domainType domain object type
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public abstract Object doConvert(JTransfo jTransfo, Object toObject, Class<Object> domainType)
            throws JTransfoException;

    @Override
    public Object convert(Object toObject, Class<Object> domainType) throws JTransfoException {
        return doConvert(jTransfo, toObject, domainType);
    }

    @Override
    public Object reverse(Object domainObject, Class<Object> toType) throws JTransfoException {
        try {
            if (null == domainObject) {
                return null;
            }
            return jTransfo.convert(domainObject, reflectionHelper.newInstance(toType));
        } catch (InstantiationException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + toType.getName() + ".", ie);
        } catch (IllegalAccessException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + toType.getName() + ".", ie);
        }
    }
}

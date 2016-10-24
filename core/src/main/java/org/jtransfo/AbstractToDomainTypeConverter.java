/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.SyntheticField;

import java.lang.reflect.Type;

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
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toType = TypeUtil.getRawClass(realToType);
        Class<?> domainType = TypeUtil.getRawClass(realDomainType);
        // TO type should be marked with @DomainClass and domain should match declared
        return jTransfo.isToClass(toType) && domainType.isAssignableFrom(jTransfo.getDomainClass(toType));
    }

    /**
     * Do the actual conversion.
     *
     * @param jTransfo jTransfo instance in use
     * @param toObject transfer object
     * @param domainField domain object field
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public abstract Object doConvert(JTransfo jTransfo, Object toObject, SyntheticField domainField, String... tags)
            throws JTransfoException;

    @Override
    public Object convert(Object toObject, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        return doConvert(jTransfo, toObject, domainField, tags);
    }

    @Override
    public Object reverse(Object domainObject, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (null == domainObject) {
            return null;
        }
        Class<?> realToType = jTransfo.getToSubType(toField.getType(), domainObject); // type cfr @DomainClassDelegate
        try {
            return jTransfo.convert(domainObject, reflectionHelper.newInstance(realToType), tags);
        } catch (InstantiationException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + realToType.getName() + '.', ie);
        } catch (IllegalAccessException ie) {
            throw new JTransfoException(CANNOT_CREATE_INSTANCE_OF + realToType.getName() + '.', ie);
        }
    }

}

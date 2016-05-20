/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Converter lists, automatically detecting the entry type from the generic parameter.
 */
public class AutomaticListTypeConverter extends AbstractListTypeConverter {

    private static final String NAME = "automaticList";

    /**
     * No-arguments constructor, jTransfo instance needs to be injected explicitly.
     */
    public AutomaticListTypeConverter() {
        super(NAME, Object.class);
    }

    /**
     * Constructor which defined custom name.
     *
     * @param name name
     */
    public AutomaticListTypeConverter(String name) {
        super(name, Object.class);
    }

    /**
     * Constructor which injects jTransfo instance.
     *
     * @param jTransfo jTransfo instance for nested conversions
     */
    public AutomaticListTypeConverter(JTransfo jTransfo) {
        super(NAME, Object.class);
        super.setJTransfo(jTransfo);
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toClass = getClass(realToType);
        Class<?> domainClass = getClass(realDomainType);
        if (!List.class.isAssignableFrom(toClass) || !List.class.isAssignableFrom(domainClass)) {
            return false;
        }
        Class<?> paramRealToType = getParamType(realToType);
        Class<?> paramRealDomainType = getParamType(realDomainType);
        if (paramRealToType == null || paramRealDomainType == null) {
            return false;
        }
        // TO type should be marked with @DomainClass and domain should match declared
        return getJTransfo().isToClass(paramRealToType)
                && paramRealDomainType.isAssignableFrom(getJTransfo().getDomainClass(paramRealToType));
    }

    /**
     * Do the actual conversion of one object.
     *
     * @param jTransfo jTransfo instance in use
     * @param toObject transfer object
     * @param domainObjectType domain object type
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
            throws JTransfoException {
        return jTransfo.convertTo(toObject, domainObjectType, tags);
    }

    @Override
    public Object doReverseOne(
            JTransfo jTransfo, Object domainObject, SyntheticField toField, Class<?> toType, String... tags)
            throws JTransfoException {
        Class<?> clazz = getParamType(toField.getGenericType());
        return jTransfo.convertTo(domainObject, jTransfo.getToSubType(clazz, domainObject), tags);
    }

    private Class<?> getClass(Type type) {
        return  (type instanceof Class ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType());
    }

    private Class<?> getParamType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return (Class<?>) p.getActualTypeArguments()[0];
        } else  if (type instanceof Class) { // try to provide a sensible default, should not be needed
            return (Class<?>) type;
        } else {
            return null;
        }
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;

import java.lang.reflect.Type;
import java.util.Set;

/**
 * Converter for sets, automatically detecting the entry type from the generic parameter.
 */
public class AutomaticSetTypeConverter extends AbstractSetTypeConverter {

    private static final String NAME = "automaticSet";

    /**
     * No-arguments constructor, jTransfo instance needs to be injected explicitly.
     */
    public AutomaticSetTypeConverter() {
        super(NAME, Object.class);
    }

    /**
     * Constructor which defined custom name.
     *
     * @param name name
     */
    public AutomaticSetTypeConverter(String name) {
        super(name, Object.class);
    }

    /**
     * Constructor which injects jTransfo instance.
     *
     * @param jTransfo jTransfo instance for nested conversions
     */
    public AutomaticSetTypeConverter(JTransfo jTransfo) {
        super(NAME, Object.class);
        super.setJTransfo(jTransfo);
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toClass = TypeUtil.getRawClass(realToType);
        Class<?> domainClass = TypeUtil.getRawClass(realDomainType);
        if (!Set.class.isAssignableFrom(toClass) || !Set.class.isAssignableFrom(domainClass)) {
            return false;
        }
        Class<?> paramRealToType = TypeUtil.getFirstTypeArgument(realToType);
        Class<?> paramRealDomainType = TypeUtil.getFirstTypeArgument(realDomainType);
        if (paramRealToType == null || paramRealDomainType == null) {
            return false;
        }
        // TO type should be marked with @DomainClass and domain should match declared
        return (getJTransfo().isToClass(paramRealToType)
                && paramRealDomainType.isAssignableFrom(getJTransfo().getDomainClass(paramRealToType)))
                || (isPrimitiveOrString(paramRealToType) && paramRealDomainType == paramRealToType);
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
        Class<?> clazz = TypeUtil.getFirstTypeArgument(toField.getGenericType());
        return jTransfo.convertTo(domainObject, jTransfo.getToSubType(clazz, domainObject), tags);
    }

}

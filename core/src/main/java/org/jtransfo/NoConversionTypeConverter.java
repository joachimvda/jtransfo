/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Fallback type converter which does no conversion at all.
 */
public class NoConversionTypeConverter implements TypeConverter<Object, Object> {

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        return realToType.getName().equals(realDomainType.getName());
    }

    @Override
    public Object convert(Object object, Class<Object> domainClass) throws JTransfoException {
        return object;
    }

    @Override
    public Object reverse(Object object, Class<Object> toClass) throws JTransfoException {
        return object;
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.reflect.Type;

import org.jtransfo.internal.SyntheticField;

/**
 * Fallback type converter which does no conversion at all.
 */
public class NoConversionTypeConverter implements TypeConverter<Object, Object> {

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        return realToType.equals(realDomainType);
    }

    @Override
    public Object convert(Object object, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        return object;
    }

    @Override
    public Object reverse(Object object, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        return object;
    }
}

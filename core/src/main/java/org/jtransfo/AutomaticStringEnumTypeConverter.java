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

/**
 * Type converter for representing enums as string in the transfer object.
 * <p>
 * This one automaticall converts between String ans any enum.
 * If you only want String-enum type conversion for specific enums, use {@link StringEnumTypeConverter}.
 * </p>
 */
public class AutomaticStringEnumTypeConverter implements TypeConverter<String, Enum> {

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toType = TypeUtil.getRawClass(realToType);
        Class<?> domainType = TypeUtil.getRawClass(realDomainType);
        return String.class.isAssignableFrom(toType) && Enum.class.isAssignableFrom(domainType);
    }

    @Override
    public Enum convert(String object, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        if (null == object || 0 == object.length()) {
            return null;
        }

        return Enum.valueOf((Class<Enum>) domainField.getType(), object);
    }

    @Override
    public String reverse(Enum object, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (null == object) {
            return null;
        }
        return object.name();
    }

}

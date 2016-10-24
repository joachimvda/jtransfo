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
 * This one needs to be configured for each enum which needs to be converted.
 * If you want this to apply for all enums, use {@link AutomaticStringEnumTypeConverter}.
 * </p>
 *
 * @param <ENUM_TYPE> enum type to convert with
 */
public class StringEnumTypeConverter<ENUM_TYPE extends Enum> implements TypeConverter<String, ENUM_TYPE> {

    private final Class<ENUM_TYPE> enumClass;

    /**
     * Construct a new type converter for the given enum type.
     *
     * @param enumClass enum type class
     */
    public StringEnumTypeConverter(Class<ENUM_TYPE> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toType = TypeUtil.getRawClass(realToType);
        Class<?> domainType = TypeUtil.getRawClass(realDomainType);
        return String.class.isAssignableFrom(toType) && enumClass.isAssignableFrom(domainType);
    }

    @Override
    public ENUM_TYPE convert(String object, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        if (null == object || 0 == object.length()) {
            return null;
        }

        return (ENUM_TYPE) Enum.valueOf(enumClass, object);
    }

    @Override
    public String reverse(ENUM_TYPE object, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (null == object) {
            return null;
        }
        return object.name();
    }

}

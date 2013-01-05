/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Field;

/**
 * Converter class to copy one field to the transfer object class.
 */
public final class ToToConverter extends AbstractConverter {

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainFields domain object field
     * @param typeConverter type converter
     */
    public ToToConverter(Field toField, Field[] domainFields, TypeConverter typeConverter) {
        super(toField, domainFields, typeConverter);
    }

    @Override
    public void doConvert(Object source, Object target)
            throws JTransfoException, IllegalAccessException, IllegalArgumentException {
        Object value = source;
        for (Field field : domainFields) {
            value = field.get(value);
        }
        Object converted = typeConverter.reverse(value, toField.getType());
        toField.set(target, converted);
    }

    @Override
    public String accessExceptionMessage() {
        return "Cannot convert domain field %s to TO field %s, field cannot be accessed.";
    }

    @Override
    public String argumentExceptionMessage() {
        return "Cannot convert domain field %s to TO field %s, field needs type conversion.";
    }
}

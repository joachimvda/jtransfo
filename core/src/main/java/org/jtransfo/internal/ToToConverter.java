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

/**
 * Converter class to copy one field to the transfer object class.
 */
public final class ToToConverter extends AbstractConverter {

    private SyntheticField toField;
    private SyntheticField[] domainFields;
    private TypeConverter typeConverter;

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainFields domain object field
     * @param typeConverter type converter
     */
    public ToToConverter(SyntheticField toField, SyntheticField[] domainFields, TypeConverter typeConverter) {
        this.toField = toField;
        this.domainFields = domainFields;
        this.typeConverter = typeConverter;
    }

    @Override
    public void doConvert(Object source, Object target)
            throws JTransfoException, IllegalAccessException, IllegalArgumentException {
        Object value = source;
        for (SyntheticField field : domainFields) {
            if (null != value) {
                value = field.get(value);
            }
        }
        Object converted = typeConverter.reverse(value, toField, target);
        toField.set(target, converted);
    }

    @Override
    public String accessExceptionMessage() {
        return String.format("Cannot convert domain field %s to TO field %s, field cannot be accessed.",
                domainFieldName(domainFields), toField.getName());
    }

    @Override
    public String argumentExceptionMessage() {
        return String.format("Cannot convert domain field %s to TO field %s, field needs type conversion.",
                domainFieldName(domainFields), toField.getName());
    }
}

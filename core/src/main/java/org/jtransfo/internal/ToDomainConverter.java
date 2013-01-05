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
 * Converter class to copy one field to the domain class.
 */
public final class ToDomainConverter extends AbstractConverter {

    private Field toField;
    private Field[] domainFields;
    private TypeConverter typeConverter;

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainFields domain object field
     * @param typeConverter type converter
     */
    public ToDomainConverter(Field toField, Field[] domainFields, TypeConverter typeConverter) {
        this.toField = toField;
        this.domainFields = domainFields;
        this.typeConverter = typeConverter;
    }

    @Override
    public void doConvert(Object source, Object firstTarget)
            throws JTransfoException, IllegalAccessException, IllegalArgumentException {
        Object value = toField.get(source);
        Object target = firstTarget;
        for (int i = 0; i < domainFields.length - 1; i++) {
            target = domainFields[i].get(target);
            if (null == target) {
                throw new JTransfoException(String.format("Cannot convert TO field %s to domain field %s, " +
                        "transitive field %s in path is null.", toField.getName(), domainFieldName(domainFields),
                        domainFields[i].getName()));
            }
        }
        Field domainField = domainFields[domainFields.length - 1];
        domainField.set(target, typeConverter.convert(value, domainField.getType()));
    }

    @Override
    public String accessExceptionMessage() {
        return String.format("Cannot convert TO field %s to domain field %s, field cannot be accessed.",
                toField.getName(), domainFieldName(domainFields));
    }

    @Override
    public String argumentExceptionMessage() {
        return String.format("Cannot convert TO field %s to domain field %s, field needs type conversion.",
                toField.getName(), domainFieldName(domainFields));
    }
}

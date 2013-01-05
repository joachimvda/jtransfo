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

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainFields domain object field
     * @param typeConverter type converter
     */
    public ToDomainConverter(Field toField, Field[] domainFields, TypeConverter typeConverter) {
        super(toField, domainFields, typeConverter);
    }

    @Override
    public void doConvert(Object source, Object firstTarget)
            throws JTransfoException, IllegalAccessException, IllegalArgumentException {
        Object value = toField.get(source);
        Object target = firstTarget;
        for (int i = 0; i < domainFields.length - 1 ; i++) {
            target = domainFields[i].get(target);
            if (null == target) {
                throw new JTransfoException(String.format("Cannot convert TO field %s to domain field %s, " +
                        "transitive field %s in path is null.", toField.getName(), domainFieldName(),
                        domainFields[i].getName()));
            }
        }
        Field domainField = domainFields[domainFields.length - 1];
        domainField.set(target, typeConverter.convert(value, domainField.getType()));
    }

    @Override
    public String accessExceptionMessage() {
        return "Cannot convert TO field %2$s to domain field %1$s, field cannot be accessed.";
    }

    @Override
    public String argumentExceptionMessage() {
        return "Cannot convert TO field %2$s to domain field %1$s, field needs type conversion.";
    }
}

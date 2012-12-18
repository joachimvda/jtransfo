/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.Converter;
import org.jtransfo.JTransfoException;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Field;

/**
 * Converter class to copy one field to the domain class.
 */
public final class ToDomainConverter implements Converter {

    private Field toField;
    private Field domainField;
    private TypeConverter typeConverter;

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainField domain object field
     * @param typeConverter type converter
     */
    public ToDomainConverter(Field toField, Field domainField, TypeConverter typeConverter) {
        this.toField = toField;
        this.domainField = domainField;
        this.typeConverter = typeConverter;
    }

    @Override
    public void convert(Object source, Object target) throws JTransfoException {
        try {
            Object value = toField.get(source);
            domainField.set(target, typeConverter.convert(value, domainField.getType()));
        } catch (IllegalAccessException iae) {
            throw new JTransfoException("Cannot convert TO field " + toField.getName() + " to domain field " +
                    domainField.getName() + ", field cannot be accessed.", iae);
        } catch (IllegalArgumentException iae) {
            throw new JTransfoException("Cannot convert TO field " + toField.getName() + " to domain field " +
                    domainField.getName() + ", field needs type conversion.", iae);
        }
    }
}

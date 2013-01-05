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
 * Converter class to copy one field to the transfer object class.
 */
public abstract class AbstractConverter implements Converter {

    protected Field toField;
    protected Field[] domainFields;
    protected TypeConverter typeConverter;

    /**
     * Constructor.
     *
     * @param toField transfer object field
     * @param domainFields domain object field
     * @param typeConverter type converter
     */
    public AbstractConverter(Field toField, Field[] domainFields, TypeConverter typeConverter) {
        this.toField = toField;
        this.domainFields = domainFields;
        this.typeConverter = typeConverter;
    }

    /**
     * Actual conversion code, exceptions handled by invoker.
     *
     * @param source source object
     * @param target target object
     * @throws JTransfoException oops
     * @throws IllegalAccessException oops
     * @throws IllegalArgumentException oops
     */
    public abstract void doConvert(Object source, Object target)
            throws JTransfoException, IllegalAccessException, IllegalArgumentException;

    /**
     * Get exception message for IllegalAccessException.
     *
     * @return message for exception with %s placeholders for source and target field name
     */
    public abstract String accessExceptionMessage();

    /**
     * Get exception message for IllegalArgumentException.
     *
     * @return message for exception with %s placeholders for source and target field name
     */
    public abstract String argumentExceptionMessage();

    @Override
    public void convert(Object source, Object target) throws JTransfoException {
        try {
            doConvert(source, target);
        } catch (IllegalAccessException iae) {
            throw new JTransfoException(String.format(accessExceptionMessage(), domainFieldName(), toField.getName()),
                    iae);
        } catch (IllegalArgumentException iae) {
            throw new JTransfoException(String.format(argumentExceptionMessage(), domainFieldName(), toField.getName()),
                    iae);
        }
    }

    protected String domainFieldName() {
        StringBuilder sb = new StringBuilder();
        sb.append(domainFields[domainFields.length - 1].getName());
        if (domainFields.length > 1) {
            sb.append(" (with path ");
            for (int i = 0; i < domainFields.length - 2; i++) {
                sb.append(domainFields[domainFields.length - i].getName());
                sb.append(".");
            }
            sb.append(domainFields[domainFields.length - 2].getName());
            sb.append(")");
        }
        return sb.toString();
    }
}

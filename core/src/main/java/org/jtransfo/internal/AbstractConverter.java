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

/**
 * Converter class to copy one field to the transfer object class.
 */
public abstract class AbstractConverter implements Converter {

    /**
     * Actual conversion code, exceptions handled by invoker.
     *
     * @param source source object
     * @param target target object
     * @param tags tags which indicate which fields can be converted based on {@link org.jtransfo.MapOnly} annotations
     * @throws JTransfoException oops
     * @throws IllegalAccessException oops
     * @throws IllegalArgumentException oops
     */
    public abstract void doConvert(Object source, Object target, String... tags)
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
    public void convert(Object source, Object target, String... tags) throws JTransfoException {
        try {
            doConvert(source, target, tags);
        } catch (IllegalAccessException iae) {
            throw new JTransfoException(accessExceptionMessage(), iae);
        } catch (IllegalArgumentException iae) {
            throw new JTransfoException(argumentExceptionMessage(), iae);
        }
    }

    /**
     * Get the domain fields is a readable way (with path indication).
     *
     * @param domainFields fields to convert
     * @return readable domain fields
     */
    protected String domainFieldName(SyntheticField[] domainFields) {
        StringBuilder sb = new StringBuilder();
        if (domainFields.length > 0) {
            sb.append(domainFields[domainFields.length - 1].getName());
            if (domainFields.length > 1) {
                sb.append(" (with path ");
                for (int i = 0; i < domainFields.length - 2; i++) {
                    sb.append(domainFields[i].getName());
                    sb.append(".");
                }
                sb.append(domainFields[domainFields.length - 2].getName());
                sb.append(")");
            }
        }
        return sb.toString();
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import java.lang.reflect.Type;

/**
 * Abstraction of a {@link java.lang.reflect.Field} which allows different handling of the field accessors.
 */
public interface SyntheticField {

    /**
     * Get field value.
     *
     * @param object object which contains the field.
     * @return field value
     * @throws IllegalAccessException illegal access
     * @throws IllegalArgumentException illegal argument
     */
    Object get(Object object) throws IllegalAccessException, IllegalArgumentException;

    /**
     * Set field value.
     *
     * @param object object which contains the field.
     * @param value field value
     * @throws IllegalAccessException illegal access
     * @throws IllegalArgumentException illegal argument
     */
    void set(Object object, Object value) throws IllegalAccessException, IllegalArgumentException;

    /**
     * Get field name.
     *
     * @return field name
     */
    String getName();

    /**
     * Get field type.
     *
     * @return field type
     */
    Class<?> getType();

    /**
     * Get field generic type.
     * 
     * @return field generic type
     */
    Type getGenericType();
}

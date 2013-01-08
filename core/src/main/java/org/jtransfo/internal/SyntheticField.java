/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import java.lang.reflect.Field;

/**
 * Abstraction of a {@link Field} which uses the getter and setter if they exist.
 */
public class SyntheticField {

    Field field;

    /**
     * Constructor.
     *
     * @param clazz clazz of which the field is part
     * @param field field (may be in a parent class of clazz)
     */
    public SyntheticField(Class<?>clazz, Field field) {
        this.field = field;
    }

    /**
     * Get field value.
     *
     * @param object object which contains the field.
     * @return field value
     * @throws IllegalAccessException illegal access
     * @throws IllegalArgumentException illegal argument
     */
    public Object get(Object object) throws IllegalAccessException, IllegalArgumentException {
        return field.get(object);
    }

    /**
     * Set field value.
     *
     * @param object object which contains the field.
     * @param value field value
     * @throws IllegalAccessException illegal access
     * @throws IllegalArgumentException illegal argument
     */
    public void set(Object object, Object value) throws IllegalAccessException, IllegalArgumentException {
        field.set(object, value);
    }

    /**
     * Get field name.
     *
     * @return field name
     */
    public String getName() {
        return field.getName();
    }

    /**
     * Get field type.
     *
     * @return field type
     */
    public Class<?> getType() {
        return field.getType();
    }
}

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
public class AccessorSyntheticField implements SyntheticField {

    private Field field;
    //private Method getter;
    //private Method setter;

    /**
     * Constructor.
     *
     * @param reflectionHelper reflection helper to use
     * @param clazz clazz of which the field is part
     * @param field field (may be in a parent class of clazz)
     */
    public AccessorSyntheticField(ReflectionHelper reflectionHelper, Class<?> clazz, Field field) {
        this.field = field;
        //getter = reflectionHelper.getMethod(getGetterName(field.getName));
        //setter = reflectionHelper.getMethod(getSetterName(field.getName), field.getType());
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

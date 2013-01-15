/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * Abstraction of a {@link Field} which uses the getter and setter if they exist.
 */
public class AccessorSyntheticField implements SyntheticField {

    private Field field;
    private Method getter;
    private Method setter;

    /**
     * Constructor.
     *
     * @param reflectionHelper reflection helper to use
     * @param clazz clazz of which the field is part
     * @param field field (may be in a parent class of clazz)
     */
    public AccessorSyntheticField(ReflectionHelper reflectionHelper, Class<?> clazz, Field field) {
        this.field = field;
        getter = reflectionHelper.getMethod(clazz, field.getType(), getGetterName(field.getName(),
                field.getType().getCanonicalName().equals(boolean.class.getCanonicalName())));
        setter = reflectionHelper.getMethod(clazz, null, getSetterName(field.getName()), field.getType());
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
        if (null != getter) {
            try {
                return getter.invoke(object);
            } catch (InvocationTargetException ite) {
                throw new JTransfoException("Trying to use " + getter.getName() + " on object of type " +
                        object.getClass().getName() + " while expected type is " +
                        getter.getDeclaringClass().getName());
            }
        } else {
            // @todo first time, log warning about not using getter (not public, wrong name or wrong type)
            return field.get(object);
        }
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
        if (null != setter) {
            try {
                setter.invoke(object, value);
            } catch (InvocationTargetException ite) {
                throw new JTransfoException("Trying to use " + setter.getName() + " on object of type " +
                        object.getClass().getName() + " while expected type is " +
                        setter.getDeclaringClass().getName());
            }
        } else {
            // @todo first time, log warning about not using getter (not public, wrong name or wrong type)
            field.set(object, value);
        }
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

    private String getGetterName(String fieldName, boolean isBoolean) {
        return (isBoolean ? "is" : "get") + capitalize(fieldName);
    }

    private String getSetterName(String fieldName) {
        return "set" + capitalize(fieldName);
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }
}

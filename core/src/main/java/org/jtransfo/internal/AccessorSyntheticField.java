/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstraction of a {@link Field} which uses the getter and setter if they exist.
 */
public class AccessorSyntheticField implements SyntheticField {

    private final Logger log = LoggerFactory.getLogger(AccessorSyntheticField.class);

    private static final String GET_SET_ITO = "InvocationTargetException trying to use %s on object of type %s. " +
            "Expected type is %s. Cause is: %s";

    private String name;
    private Field field;
    private Method getter;
    private Method setter;
    private boolean getUsingFieldLogged;
    private boolean setUsingFieldLogged;

    /**
     * Constructor, access field using getter and setter if exist.
     *
     * @param reflectionHelper reflection helper to use
     * @param clazz clazz of which the field is part
     * @param field field (may be in a parent class of clazz)
     */
    AccessorSyntheticField(ReflectionHelper reflectionHelper, Class<?> clazz, Field field) {
        this.field = field;
        getter = findGetter(reflectionHelper, clazz, field.getType(), field.getName());
        setter = reflectionHelper.getMethod(clazz, null, getSetterName(field.getName()), field.getType());
        name = field.getName();
    }

    /**
     * Constructor, there is no field, just use getter (and setter if not readOnly).
     *
     * @param reflectionHelper reflection helper to use
     * @param clazz clazz of which the field is part
     * @param name field name
     * @param readOnlyField is a getter sufficient
     * @throws JTransfoException can not find getter for given name or
     */
    AccessorSyntheticField(
            ReflectionHelper reflectionHelper, Class<?> clazz, String name, boolean readOnlyField)
            throws JTransfoException {
        this.field = null;
        getter = findGetter(reflectionHelper, clazz, null, name);
        if (null == getter) {
            throw new JTransfoException("Cannot find getter from " + getGetterNameAlternatives(name) + " on class " +
                    clazz.getName() + ".");
        }
        if (!readOnlyField) {
            Class<?> type = getter.getReturnType();
            setter = reflectionHelper.getMethod(clazz, null, getSetterName(name), type);
            if (null == setter) {
                throw new JTransfoException("Cannot find setter " + getSetterName(name) + " on class " +
                        clazz.getName() + ".");
            }
        }
        this.name = name;
    }

    private Method findGetter(ReflectionHelper reflectionHelper, Class<?> clazz, Class<?> returnType, String name) {
        for (String tryName : getGetterNameAlternatives(name)) {
            Method method = reflectionHelper.getMethod(clazz, null, tryName);
            if (null != method) {
                return method;
            }
        }
        return null;
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
                if (ite.getCause() instanceof RuntimeException && !(ite.getCause() instanceof JTransfoException)) {
                    throw (RuntimeException) ite.getCause();
                }
                throw new JTransfoException(String.format(GET_SET_ITO, getter.getName(), object.getClass().getName(),
                        getter.getDeclaringClass().getName(), ite.getCause().getMessage()), ite.getCause());
            }
        } else {
            if (!getUsingFieldLogged) {
                log.warn("Cannot find getter (not public, wrong name or wrong type), "
                                + "using field to access field {} of {}.", name, field.getType().getName());
                getUsingFieldLogged = true;
            }
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
                if (ite.getCause() instanceof RuntimeException && !(ite.getCause() instanceof JTransfoException)) {
                    throw (RuntimeException) ite.getCause();
                }
                throw new JTransfoException(String.format(GET_SET_ITO, setter.getName(), object.getClass().getName(),
                        setter.getDeclaringClass().getName(), ite.getCause().getMessage()), ite.getCause());
            }
        } else {
            if (!setUsingFieldLogged) {
                log.warn("Cannot find setter (not public, wrong name or wrong type), "
                                + "using field to access field {} of {}.", name, field.getType().getName());
                setUsingFieldLogged = true;
            }
            field.set(object, value);
        }
    }

    /**
     * Get field name.
     *
     * @return field name
     */
    public String getName() {
        return name;
    }

    /**
     * Get field type.
     *
     * @return field type
     */
    public Class<?> getType() {
        if (null != field) {
            return field.getType();
        } else {
            return getter.getReturnType();
        }
    }

    /**
     * Get field generic type.
     * 
     * @return field generic type
     */
    public Type getGenericType() {
        if (null != field) {
            return field.getGenericType();
        } else {
            return getter.getGenericReturnType();
        }
    }

    /**
     * Get list of possible getter names for a field (without ability to assume type).
     * Method is protected to allow test access.
     *
     * @param fieldName name of field to get possible getters for
     * @return list of possible getter names
     */
    protected List<String> getGetterNameAlternatives(String fieldName) {
        String base = capitalize(fieldName);
        List<String> res = new ArrayList<>();
        res.add("get" + capitalize(fieldName));
        res.add(fieldName);
        String alt = "is" + base;
        if (alt.startsWith("isIs")) {
            res.add("i" + alt.substring(3));
        }
        res.add(alt);
        alt = "has" + base;
        if (!alt.startsWith("hasIs")) {
            if (alt.startsWith("hasHas")) {
                res.add("h" + alt.substring(4));
            } else {
                res.add(alt);
            }
        }
        return res;
    }

    private String getSetterName(String fieldName) {
        return "set" + capitalize(fieldName);
    }

    private String capitalize(String name) {
        return name.substring(0, 1).toUpperCase(Locale.ENGLISH) + name.substring(1);
    }
}

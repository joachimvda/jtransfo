/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Helper class for all things class manipulation and reflection.
 */
public class ReflectionHelper {

    /**
     * Create a new instance of a class.
     * <p>
     * Provided to allow mocking, making it possible to test the exception handling.
     * </p>
     *
     * @param clazz class to create new instance of
     * @param <T> type of object to create
     * @return new class instance
     * @throws IllegalAccessException see {@link Class#newInstance()}
     * @throws InstantiationException see {@link Class#newInstance()}
     */
    public <T> T newInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    /**
     * Create a new instance of a class given the class name.
     * <p>
     * Provided to allow mocking, making it possible to test the exception handling.
     * </p>
     *
     * @param className name of class to create new instance of
     * @param <T> type of object to create
     * @return new class instance
     * @throws IllegalAccessException see {@link Class#newInstance()}
     * @throws InstantiationException see {@link Class#newInstance()}
     * @throws ClassNotFoundException see {@link ClassLoader#loadClass(String)}
     */
    public <T> T newInstance(String className)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return newInstance(this.<T>loadClass(className));
    }

    /**
     * Load class with given name from the correct class loader.
     *
     * @param name name of class to load
     * @param <T> type of object to create
     * @return class instance
     * @throws ClassNotFoundException see {@link ClassLoader#loadClass(String)}
     */
    public <T> Class<T> loadClass(String name) throws ClassNotFoundException {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (null == cl) {
            cl = ToHelper.class.getClassLoader();
        }
        return (Class<T>) cl.loadClass(name);
    }

    /**
     * Find all declared fields of a class. Fields which are hidden by a child class are not included.
     *
     * @param clazz class to find fields for
     * @return list of fields
     */
    public List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (!fieldNames.contains(field.getName())) {
                    fieldNames.add(field.getName());
                    makeAccessible(field);
                    result.add(field);
                }
            }
            searchType = searchType.getSuperclass();
        }
        return result;
    }

    /**
     * Convert list of (real) fields to synthetic fields (which use accessor methods).
     *
     * @param clazz class which contains the fields
     * @param fields fields to convert to synthetic fields
     * @return list of synthetic fields
     */
    public List<SyntheticField> makeSynthetic(Class<?> clazz, List<Field> fields) {
        List<SyntheticField> result = new ArrayList<>();
        for (Field field : fields) {
            result.add(new AccessorSyntheticField(this, clazz, field));
        }
        return result;
    }

    /**
     * Find all declared (synthetic) fields of a class.
     *
     * @param clazz class to find fields for
     * @return list of fields
     */
    public List<SyntheticField> getSyntheticFields(Class<?> clazz) {
        return makeSynthetic(clazz, getFields(clazz));
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     * <p>
     * This method is borrowed from Spring's ReflectionUtil class.
     * </p>
     *
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    public void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Get method with given name and parameters and given return type.
     *
     * @param type class on which method should be found
     * @param returnType required return type (or null for void or no check)
     * @param name method name
     * @param parameters method parameter types
     * @return method or null when method not found
     */
    public Method getMethod(Class<?> type, Class<?> returnType, String name, Class<?>... parameters) {
        try {
            Method method = type.getMethod(name, parameters);
            if (null != returnType && !returnType.isAssignableFrom(method.getReturnType())) {
                method = null;
            }
            return method;
        } catch (NoSuchMethodException nsme) {
            return null;
        }
    }

}

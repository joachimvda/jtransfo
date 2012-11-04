/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import java.lang.reflect.Field;
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
     * <p/>
     * Provided to allow mocking, making it possible to test the exception handling.
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
     * <p/>
     * Provided to allow mocking, making it possible to test the exception handling.
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
        List<Field> result = new ArrayList<Field>();
        Set<String> fieldNames = new HashSet<String>();
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
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     * <p/>
     * This method is borrowed from Spring's ReflectionUtil class.
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

}

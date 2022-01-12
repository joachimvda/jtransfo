/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class for all things class manipulation and reflection.
 */
public class ReflectionHelper {

    private final Logger log = LoggerFactory.getLogger(ReflectionHelper.class);

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
    <T> Class<T> loadClass(String name) throws ClassNotFoundException {
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
    List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        Set<String> fieldNames = new HashSet<>();
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (!fieldNames.contains(field.getName())) {
                    fieldNames.add(field.getName());
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
    List<SyntheticField> makeSynthetic(Class<?> clazz, List<Field> fields) {
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
    List<SyntheticField> getSyntheticFields(Class<?> clazz) {
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
    void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    /**
     * Make the field inside the given synthetic field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     * <p>
     * This method is borrowed from Spring's ReflectionUtil class.
     * </p>
     *
     * @param syntheticField the synthetic field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    void makeAccessible(SyntheticField syntheticField) {
        syntheticField.getField().ifPresent(this::makeAccessible);
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     * <p>
     * This method is borrowed from Spring's ReflectionUtil class.
     * </p>
     *
     * @param method the method to make accessible
     * @see java.lang.reflect.Method#setAccessible
     */
    private void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers())
                || !Modifier.isPublic(method.getDeclaringClass().getModifiers())
                || Modifier.isFinal(method.getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
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
    Method getMethod(Class<?> type, Class<?> returnType, String name, Class<?>... parameters) {
        Method method = null;
        try {
            // first try for public methods
            method = type.getMethod(name, parameters);
            if (null != returnType && !returnType.isAssignableFrom(method.getReturnType())) {
                method = null;
            }
        } catch (NoSuchMethodException nsme) {
            // ignore
            log.trace(nsme.getMessage(), nsme);
        }
        if (null == method) {
            method = getNonPublicMethod(type, returnType, name, parameters);
        }
        return method;
    }

    private Method getNonPublicMethod(Class<?> type, Class<?> returnType, String name, Class<?>... parameters) {
        Method method = null;
        Class<?> searchType = type;
        while (null == method && null != searchType && !Object.class.equals(searchType)) {
            try {
                method = searchType.getDeclaredMethod(name, parameters);
                if (null != returnType && !returnType.isAssignableFrom(method.getReturnType())) {
                    method = null;
                }
            } catch (NoSuchMethodException nsme) {
                // ignore
                log.trace(nsme.getMessage(), nsme);
            }
            if (null != method) {
                makeAccessible(method);
            } else {
                searchType = searchType.getSuperclass();
            }
        }
        return method;
    }

    /**
     * Get the annotations of given type which are available on the annotated element. Considers both the annotations
     * on the element and the meta-annotations (annotations on the annotations).
     * <p>The result is given in no specific order</p>
     *
     * @param element annotated element
     * @param annotation annotation to find
     * @param <T> annotation type
     * @return annotation
     */
    <T extends Annotation> List<T> getAnnotationWithMeta(AnnotatedElement element, Class<T> annotation) {
        return (List) Stream.of(element.getDeclaredAnnotations())
                .flatMap(this::addMetaAnnotations)
                .filter(a -> annotation.isAssignableFrom(a.annotationType()))
                .collect(Collectors.toList());
    }

    private Stream<Annotation> addMetaAnnotations(Annotation a) {
        Set<Annotation> res = new HashSet<>();
        addMetaAnnotations(res, a);
        return res.stream();
    }

    private void addMetaAnnotations(Set<Annotation> set, Annotation annotation) {
        if (set.add(annotation)) { // set is needed or continues infinitely
            for (Annotation meta : annotation.annotationType().getDeclaredAnnotations()) {
                addMetaAnnotations(set, meta);
            }
        }
    }

}

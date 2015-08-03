/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.util.List;

/**
 * jTransfo main access point.
 */
public interface JTransfo {

    /**
     * Default tag which is activated when not tags are specified in the convert call.
     */
    String DEFAULT_TAG_WHEN_NO_TAGS = "§#noTags";

    /**
     * Fill the target object with the values from the source object.
     * <p/>
     * This will write all values from the transfer object, other fields are not touched.
     *
     * @param source source object. Should not be null.
     * @param target target object. Should not be null.
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     *      Tags are processed from left to right.
     * @param <T> type of object for target
     * @return target object
     */
    <T> T convert(Object source, T target, String... tags);

    /**
     * Create a new domain object from the source transfer object.
     * <p/>
     * When the source is null, the result is also null.
     * <p/>
     * The object finders are used to build the object to copy to.
     *
     * @param source source transfer object
     * @return domain object
     */
    Object convert(Object source);

    /**
     * Create a new domain object from the source transfer object.
     * <p/>
     * When the source is null, the result is also null.
     * <p/>
     * The object finders are used to build the object to copy to.
     *
     * @param source source transfer object
     * @param targetClass target class to convert to
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     *      Tags are processed from left to right.
     * @param <T> type of object for target
     * @return domain object
     */
    <T> T convertTo(Object source, Class<T> targetClass, String... tags);

    /**
     * Convert a list of object to the given type. Applies {@link #convertTo(Object, Class, String...)} on each object.
     * <p/>
     * When the source is null, the result is also null.
     *
     * @param source source list of objects
     * @param targetClass target class to convert each object to
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     *      Tags are processed from left to right.
     * @param <T> type of object for target
     * @return list of target objects
     */
    <T> List<T> convertList(List<?> source, Class<T> targetClass, String... tags);

    /**
     * Get the base target (domain) object for the source (transfer) object.
     * <p/>
     * When the source is null, the result is also null.
     * <p/>
     * The object finders are used to build the object to copy to.
     *
     * @param source source transfer object
     * @param targetClass target class to convert to
     * @param <T> type of object for target
     * @return domain object
     */
    <T> T findTarget(Object source, Class<T> targetClass);

    /**
     * Get domain class for transfer object.
     *
     * @param toClass transfer object class
     * @return domain class as annotated on class
     */
    Class<?> getDomainClass(Class<?> toClass);

    /**
     * Is the given class a transfer object class?
     * <p/>
     * True when there is a {@link DomainClass} annotation on the class.
     *
     * @param toClass object class to test
     * @return true when object is a transfer object
     */
    boolean isToClass(Class<?> toClass);

    /**
     * Get the correct transfer object type for the given domain object.
     * <p/>
     * This searches the DomainClassDelegates (if present) to see of there is a better matching transfer object than
     * the one given as parameter.
     *
     * @param toType base transfer object type
     * @param domainObject domain object (instance)
     * @return proper transfer object type to use
     */
     Class<?> getToSubType(Class<?> toType, Object domainObject);
}
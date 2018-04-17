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
 * <p>
 * jTransfo can be used to convert between transfer objects (TO ot DTO) and domain objects.
 * </p><p>
 * You just have to annotate the transfer object with {@link DomainClass} to indicate the domain object
 * (either the class or the fully qualified class name).
 * </p><p>
 * By default all fields are converted, but you can use annotations to configure this:
 * <ul>
 *     <li>{@link MappedBy}</li>
 *     <li>{@link NotMapped}</li>
 *     <li>{@link MapOnly}</li>
 *     <li>{@link ReadOnly}</li>
 *     <li>{@link ReadOnlyDomain}</li>
 * </ul>
 * </p><p>
 * You can also add {@link PreConvert} or {@link PostConvert} tags on the transfer object to define code to be executed
 * before or after converting the fields.
 * </p><p>
 * When converting, the fields on the transfer objects are always used directly (as the annotations are on the fields).
 * For domain objects, the getters and setters are used when they exist (even when private).
 * </p><p>
 * When jTransfo needs to determine the object to convert to (when using {@link #convertTo(Object, Class, String...)}
 * or when converting a nested object which is null in the target) then the {@link ObjectFinder} will be used.
 * By default this always creates new objects, but you can add custom object finders to allow objects to be retrieved
 * from the database or whatever behaviour you need.
 * </p><p>
 * The conversion itself can be controlled in various ways:
 * <ul>
 *     <li>When converting you can pass tags which can configure the conversion. These are considered for
 *         {@link MapOnly} and in the type converters, interceptors etc</li>
 *     <li>You can define type converters which are applied aither automatically or declared in the {@link MappedBy}
 *         or {@link MapOnly} annotations. See {@link TypeConverter}.</li>
 *     <li>You can add an interceptor to the conversion. This can be used in various way, for example to add
 *         validation or side-effect like auditing. See {@link ConvertInterceptor}.
 *     </li>
 *     <li>You may also want to provide a {@link ObjectReplacer}. This can replace objects just before they are
 *         converted. This can for example be useful to detect lazy objects and replace them with proper
 *         implementations. See {@link ObjectReplacer}.
 *     </li>
 *     <li>You may also want to provide a {@link ClassReplacer}. This can replace classes which are used as conversion
 *         target. It affects both {@link DomainClass} target classes and {@link #convertTo(Object, Class, String...)}
 *         target classes.
 *         This is very useful to allow replacing interfaces to implementation classes for domain objects or replacing
 *         transfer object classes to enhanced transfer objects (with more fields).
 *         You should only replace classes by child classes. See {@link ClassReplacer}.
 *     </li>
 * </ul>
 * </p>
 */
public interface JTransfo {

    /**
     * Default tag which is activated when not tags are specified in the convert call.
     */
    String DEFAULT_TAG_WHEN_NO_TAGS = "§#noTags";

    /**
     * Fill the target object with the values from the source object.
     * <p>
     * This will write all values from the transfer object, other fields are not touched.
     * </p>
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
     * <p>
     * When the source is null, the result is also null.
     * </p><p>
     * The object finders are used to build the object to copy to.
     * </p>
     *
     * @param source source transfer object
     * @return domain object
     */
    Object convert(Object source);

    /**
     * Create a new domain object from the source transfer object.
     * <p>
     * When the source is null, the result is also null.
     * </p><p>
     * The object finders are used to build the object to copy to.
     * </p>
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
     * <p>
     * When the source is null, the result is also null.
     * </p>
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
     * <p>
     * When the source is null, the result is also null.
     * </p><p>
     * The object finders are used to build the object to copy to.
     * </p>
     *
     * @param source source transfer object
     * @param targetClass target class to convert to
     * @param tags tags which indicate how objects need to be found
     * @param <T> type of object for target
     * @return domain object
     */
    <T> T findTarget(Object source, Class<T> targetClass, String... tags);

    /**
     * Get domain class for transfer object.
     *
     * @param toClass transfer object class
     * @return domain class as annotated on class
     */
    Class<?> getDomainClass(Class<?> toClass);

    /**
     * Is the given class a transfer object class?
     * <p>
     * True when there is a {@link DomainClass} annotation on the class.
     * </p>
     *
     * @param toClass object class to test
     * @return true when object is a transfer object
     */
    boolean isToClass(Class<?> toClass);

    /**
     * Get the correct transfer object type for the given domain object.
     * <p>
     * This searches the DomainClassDelegates (if present) to see of there is a better matching transfer object than
     * the one given as parameter.
     * </p>
     *
     * @param toType base transfer object type
     * @param domainObject domain object (instance)
     * @return proper transfer object type to use
     */
     Class<?> getToSubType(Class<?> toType, Object domainObject);
}
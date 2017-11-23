/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Interceptor which allows additional work to be done just after jTransfo converts between objects.
 * <p>
 * This is called at the end of the interceptor pipeline, just after conversion of the individual fields.
 * The postconverter is only used when it is declared on the transfer object using {@link PostConvert}.
 * </p><p>
 * The postconverter should be re-entrant. A singleton is used for all convert invocations.
 * </p>
 *
 * @param <T> type of the transfer object
 * @param <D> type of the domain object
 */
public interface PostConverter<T, D> {

    /**
     * Do some work just after doing the actual conversion from domain to transfer object.
     *
     * @param source source object. Should not be null.
     * @param target target object. Should not be null.
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     *      Tags are processed from left to right.
     */
    default void postConvertToTo(D source, T target, String... tags) {
    }

    /**
     * Do some work just after doing the actual conversion from transfer to domain object.
     * <p>
     * You can prevent the conversion of the actual object to happen by returning {@link PreConverter.Result#SKIP}.
     * </p>
     *
     * @param source source object. Should not be null.
     * @param target target object. Should not be null.
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     *      Tags are processed from left to right.
     */
    default void postConvertToDomain(T source, D target, String... tags) {
    }

}
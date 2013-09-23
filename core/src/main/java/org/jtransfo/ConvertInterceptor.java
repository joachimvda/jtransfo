/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Interceptor which allows additional work to be done when jTransfo converts between objects.
 * <p/>
 * The functions like around advice in AOP.
 * <p/>
 * The interceptor should be re-entrant. A singleton may be used for all convert invocations.
 * <p/>
 * The interceptors can for example be used to handle validation.
 */
public interface ConvertInterceptor {

    /**
     * Interceptor for {@link JTransfo#convert(Object, Object, String...)}.
     *
     * @param source source to convert from
     * @param target object to convert to
     * @param isTargetTo is the target class the transfer object?
     * @param next next in the interceptor chain, handles the actual convert
     * @param tags tags for the conversion
     * @param <T> type of object to convert to
     * @return target object after convert
     */
    <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags);

}

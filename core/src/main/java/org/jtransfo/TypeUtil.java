/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

/**
 * Utility class for working with types.
 */
public final class TypeUtil {

    private TypeUtil() {
        throw new IllegalAccessError("TypeUtil cannot be instantiated.");
    }

    /**
     * Get the main class (raw type) for a {@link Type}.
     *
     * @param type type to get class for
     * @return class
     */
    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof TypeVariable) {
            Type[] bounds = ((TypeVariable) type).getBounds();
            if (null != bounds && 0 < bounds.length) {
                return getRawClass(bounds[0]);
            }
        }
        return Object.class;
    }

    /**
     * Get declared type parameter of the type has one.
     *
     * @param type type
     * @return declared parameter class
     */
    public static Class<?> getFirstTypeArgument(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return getRawClass(p.getActualTypeArguments()[0]);
        } else {
            return null;
        }
    }

}

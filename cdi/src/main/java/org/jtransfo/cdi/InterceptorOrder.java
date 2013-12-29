/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi;

/**
 * Allow interceptors to be annotated with the order in which they should be applied.
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.TYPE,
        java.lang.annotation.ElementType.METHOD,
        java.lang.annotation.ElementType.FIELD })
public @interface InterceptorOrder {

    /** Order. Higher values are put later in the chain. */
    int value() default DEFAULT_ORDER;

    /** Default order value. Also applied to interceptors without annotation. */
    int DEFAULT_ORDER = Integer.MAX_VALUE;

}

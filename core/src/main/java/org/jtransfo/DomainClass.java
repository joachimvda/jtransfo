/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify the domain class on the transfer object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface DomainClass {

    /**
     * Fully qualified class name for the domain object.
     * <p/>
     * Using this allows you to avoid a compile dependency on the class name but makes the connection more brittle
     * when refactoring. The value is ignored if {@link #domainClass()} is set.
     */
    String value() default DEFAULT_NAME;

    /**
     * Class for te domain object.
     * <p/>
     * This requires a compile dependency on the domain class. The value has precedence over the {@link #value()}
     * field.
     */
    Class domainClass() default DefaultClass.class;

    /**
     * Default value for {@link #value} indicating that {@link #domainClass} should be set.
     */
    String DEFAULT_NAME = "?";

    /**
     * Default value for {@link #domainClass} indicating that {@link #value} should be set.
     */
    class DefaultClass {
    }
}

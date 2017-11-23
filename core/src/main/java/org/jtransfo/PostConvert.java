/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify a {@link PostConverter} to be used when converting.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Repeatable(PostConvert.List.class)
@Documented
public @interface PostConvert {

    /**
     * Name for the type converter, this is either name (when it implements {@link Named} or the
     * Fully qualified class name.
     * <p>
     * Using this allows you to avoid a compile dependency on the class name but makes the connection more brittle
     * when refactoring. The value is ignored if {@link #converterClass()} is set.
     * </p>
     */
    String value() default DEFAULT_NAME;

    /**
     * Class to use for postconvert.
     * <p>
     * This requires a compile dependency on the domain class. The value has postcedence over {@link #value()}.
     * </p>
     */
    Class converterClass() default PostConvert.class;

    /**
     * Default value for {@link #value} indicating that {@link #converterClass()} should be set.
     */
    String DEFAULT_NAME = "?";

    /**
     * Container for a list of postconverters.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
    @Documented
    public @interface List {
        /**
         * Actual list.
         * @return list
         */
        PostConvert[] value();
    }

}

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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.jtransfo.MappedBy.DEFAULT_FIELD;
import static org.jtransfo.MappedBy.DEFAULT_PATH;

/**
 * Annotation which allows security filtering on jTransfo field behaviour. If a field has a MapOnly or {@link MapOnlies}
 * annotation then the field will only be copied when one of the tags in the MapOnly or MapOnlies annotation is present.
 * In that case the {@link MappedBy} annotation on the field will only be used to provide defaults for field/path or type converter.
 *
 * <p>
 * By default fields are mapped in both directions between fields of the same name.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface MapOnly {

    /**
     * Tags for which this annotation applies.
     */
    String[] value();

    /**
     * Name of the field in the domain class.
     * <p>
     * By default the field is assumed to have the same name in the domain class.
     * </p>
     */
    String field() default DEFAULT_FIELD;

    /**
     * Path to the field when it is transitive.
     * <p>
     * By default the path is empty, meaning that the field is inside the domain object itself.
     * </p>
     */
    String path() default DEFAULT_PATH;

    /**
     * When set, the field will not be written in the domain class when the tag is present.
     */
    boolean readOnly() default false;

    /**
     * Fully qualified class name for the type converter.
     * <p>
     * Using this allows you to override the type converter on the {@link MappedBy} annotation.
     * </p>
     */
    String typeConverter() default MappedBy.DEFAULT_TYPE_CONVERTER;

    /**
     * Class to use for type conversion.
     * <p>
     * Using this allows you to override the type converter on the {@link MappedBy} annotation.
     * </p><p>
     * This requires a compile dependency on the domain class. The value has precedence over {@link #typeConverter()}.
     * </p>
     */
    Class typeConverterClass() default MappedBy.DefaultTypeConverter.class;

    /**
     * Tag which can be used to assure it is always called (handled before the specific cases).
     */
    String ALWAYS = "";
}

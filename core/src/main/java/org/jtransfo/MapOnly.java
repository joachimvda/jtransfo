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

/**
 * Annotation which allows security filtering on jTransfo field behaviour. If a field has a MapOnly of MapOnlies
 * annotation then the field will only be copied when one of the tags in the MapOnly or MapOnlies annotation is present.
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

}

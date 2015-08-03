/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Core jTransfo convert method.
 */
public interface ConvertSourceTarget {

    /**
     * Fill the target object with the values from the source object.
     * <p>
     * This will write all values from the transfer object, other fields are not touched.
     * </p>
     *
     * @param source source object. Should not be null.
     * @param target target object. Should not be null.
     * @param isTargetTo is the target class the transfer object?
     * @param tags tags which indicate which fields can be converted based on {@link org.jtransfo.MapOnly} annotations.
     *      Tags are processed from left to right.
     * @param <T> type of object for target
     * @return target object
     */
    <T> T convert(Object source, T target, boolean isTargetTo, String... tags);

}
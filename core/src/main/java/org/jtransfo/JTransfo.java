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

/**
 * jTransfo main access point.
 */
public interface JTransfo {

    /**
     * Fill the target object with the values from the source object.
     * <p/>
     * This will write all values from the transfer object, other fields are not touched.
     *
     * @param source source object
     * @param target target object
     */
    public <T> T convert(Object source, T target);

    /**
     * Create a new domain object from the source transfer object.
     * <p/>
     * This only works if the domain object has a no-arguments constructor.
     *
     * @param source source transfer object
     * @return domain object
     */
    Object convert(Object source);

}
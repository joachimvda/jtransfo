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
 * Converter, typically converts one field from the source object to a target object.
 */
public interface Converter {

    /**
     * Conversion from (part of) the source object to the target object.
     *
     * @param source source to read from
     * @param target  target to write into
     */
    void convert(Object source, Object target);
}

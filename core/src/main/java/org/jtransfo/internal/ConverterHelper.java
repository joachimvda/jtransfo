/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.ToConverter;

/**
 * Helper class for building the converters for a pair of classes.
 */
public class ConverterHelper {

    /**
     * Build the descriptor for conversion between given object types.
     *
     * @param to transfer object class, contains the annotations for the conversion @todo should be Class
     * @param domain domain class as other side of conversion @todo should be class
     * @return conversion descriptor
     */
    public ToConverter getToConverter(Object to, Object domain) {
        ToConverter converter = new ToConverter();

        return converter;
    }
}

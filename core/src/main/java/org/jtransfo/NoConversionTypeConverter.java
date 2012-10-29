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
 * Fallback type converter which does no conversion at all.
 */
public class NoConversionTypeConverter implements TypeConverter<Object, Object> {

    @Override
    public Object convert(Object object) throws JTransfoException {
        return object;
    }

    @Override
    public Object reverse(Object object) throws JTransfoException {
        return object;
    }
}

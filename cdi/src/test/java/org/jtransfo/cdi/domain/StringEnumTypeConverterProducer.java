/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi.domain;

import org.jtransfo.StringEnumTypeConverter;
import org.jtransfo.object.Gender;

import javax.enterprise.inject.Produces;

/**
 * {@link StringEnumTypeConverter} for {@link Gender}.
 */
public class StringEnumTypeConverterProducer {

    @Produces
    public StringEnumTypeConverter<Gender> getGenderTypeConverter() {
        return new StringEnumTypeConverter<>(Gender.class);
    }
}

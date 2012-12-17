/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class NoConversionTypeConverterTest {

    private TypeConverter<Object, Object> typeConverter;

    @Before
    public void setup() {
        typeConverter = new NoConversionTypeConverter();
    }

    @Test
    public void testCanConvert() throws Exception {
        assertThat(typeConverter.canConvert(Boolean.class, Boolean.class)).isTrue();
        assertThat(typeConverter.canConvert(Boolean.class, Integer.class)).isFalse();
    }

    @Test
    public void testConvert() throws Exception {
        Object obj = new Object();
        assertThat(typeConverter.convert(obj)).isEqualTo(obj);
    }

    @Test
    public void testReverse() throws Exception {
        Object obj = new Object();
        assertThat(typeConverter.reverse(obj)).isEqualTo(obj);
    }
}

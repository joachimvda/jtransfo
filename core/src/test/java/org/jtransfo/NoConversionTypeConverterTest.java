/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class NoConversionTypeConverterTest {

    private TypeConverter<Object, Object> typeConverter;

    @Mock
    private SyntheticField field;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

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
        assertThat(typeConverter.convert(obj, field, null)).isEqualTo(obj);
    }

    @Test
    public void testReverse() throws Exception {
        Object obj = new Object();
        assertThat(typeConverter.reverse(obj, field, null)).isEqualTo(obj);
    }
}

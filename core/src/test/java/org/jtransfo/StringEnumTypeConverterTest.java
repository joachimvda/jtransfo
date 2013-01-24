/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;
import org.jtransfo.object.Gender;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test for StringEnumTypeConverterTest.
 */
public class StringEnumTypeConverterTest {

    private TypeConverter<String, Gender> enumTypeConverter;

    @Mock
    private SyntheticField genderField;

    @Mock
    private SyntheticField stringField;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        enumTypeConverter = new StringEnumTypeConverter(Gender.class);

        when(genderField.getType()).thenReturn((Class) Gender.class);
        when(stringField.getType()).thenReturn((Class) String.class);
    }

    @Test
    public void testCanConvert() throws Exception {
        assertThat(enumTypeConverter.canConvert(String.class, Gender.class)).isTrue();
        assertThat(enumTypeConverter.canConvert(Object.class, Gender.class)).isFalse();
        assertThat(enumTypeConverter.canConvert(String.class, String.class)).isFalse();
        assertThat(enumTypeConverter.canConvert(Gender.class, String.class)).isFalse();
    }

    @Test
    public void testConvert() throws Exception {
        assertThat(enumTypeConverter.convert(Gender.MALE.name(), genderField, null)).isEqualTo(Gender.MALE);
        assertThat(enumTypeConverter.convert(Gender.FEMALE.name(), genderField, null)).isEqualTo(Gender.FEMALE);
        assertThat(enumTypeConverter.convert(null, genderField, null)).isNull();
        assertThat(enumTypeConverter.convert("", genderField, null)).isNull();

        exception.expect(IllegalArgumentException.class);
        assertThat(enumTypeConverter.convert("blabla", genderField, null));
    }

    @Test
    public void testReverse() throws Exception {
        assertThat(enumTypeConverter.reverse(Gender.MALE, stringField, null)).isEqualTo(Gender.MALE.name());
        assertThat(enumTypeConverter.reverse(Gender.FEMALE, stringField, null)).isEqualTo(Gender.FEMALE.name());
        assertThat(enumTypeConverter.reverse(null, stringField, null)).isNull();
    }

}

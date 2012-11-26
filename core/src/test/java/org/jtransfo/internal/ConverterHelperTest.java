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

import org.jtransfo.JTransfoException;
import org.jtransfo.MappedBy;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.TypeConverter;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ConverterHelper}.
 */
public class ConverterHelperTest {

    private ConverterHelper converterHelper;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        converterHelper = new ConverterHelper();

        ReflectionTestUtils.setField(converterHelper, "reflectionHelper", reflectionHelper);
    }

    @Test
    public void testFindField() throws Exception {
        List<Field> fields = new ArrayList<Field>();
        Field f1 = SimpleExtendedDomain.class.getDeclaredField("b");
        Field f2 = SimpleExtendedDomain.class.getDeclaredField("c");
        fields.add(f1);
        fields.add(f2);

        Field res;
        res = converterHelper.findField(fields, "c");
        assertThat(res).isEqualTo(f2);

        res = converterHelper.findField(fields, "bla");
        assertThat(res).isNull();
    }

    @Test
    public void testGetDeclaredTypeConverterNull() throws Exception {
        assertThat(converterHelper.getDeclaredTypeConverter(null)).isNull();
    }

    @Test
    public void testGetDeclaredTypeConverterDefaults() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(MappedBy.DEFAULT_TYPE_CONVERTER);
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);

        assertThat(converterHelper.getDeclaredTypeConverter(mappedBy)).isNull();
    }

    @Test
    public void testGetDeclaredTypeConverterAsClass() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(NoConversionTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(MappedBy.DEFAULT_TYPE_CONVERTER);
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenReturn(new NoConversionTypeConverter());

        TypeConverter res = converterHelper.getDeclaredTypeConverter(mappedBy);

        assertThat(res).isInstanceOf(NoConversionTypeConverter.class);

        TypeConverter res2 = converterHelper.getDeclaredTypeConverter(mappedBy);

        assertThat(res2 == res).isTrue(); // instance needs to be cached and reused
    }

    @Test
    public void testGetDeclaredTypeConverterAsName() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(NoConversionTypeConverter.class.getName());
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenReturn(new NoConversionTypeConverter());

        TypeConverter res = converterHelper.getDeclaredTypeConverter(mappedBy);

        assertThat(res).isInstanceOf(NoConversionTypeConverter.class);
    }

    @Test
    public void testGetDeclaredTypeConverterCnfe() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(NoConversionTypeConverter.class.getName());
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenThrow(new ClassNotFoundException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Declared TypeConverter class org.jtransfo.NoConversionTypeConverter cannot be found.");
        converterHelper.getDeclaredTypeConverter(mappedBy);
    }

    @Test
    public void testGetDeclaredTypeConverterIe() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(NoConversionTypeConverter.class.getName());
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage(
                "Declared TypeConverter class org.jtransfo.NoConversionTypeConverter cannot be instantiated.");
        converterHelper.getDeclaredTypeConverter(mappedBy);
    }

    @Test
    public void testGetDeclaredTypeConverterIae() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(NoConversionTypeConverter.class.getName());
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenThrow(new IllegalAccessException());

        exception.expect(JTransfoException.class);
        exception.expectMessage(
                "Declared TypeConverter class org.jtransfo.NoConversionTypeConverter cannot be accessed.");
        converterHelper.getDeclaredTypeConverter(mappedBy);
    }

    @Test
    public void testGetDeclaredTypeConverterCce() throws Exception {
        MappedBy mappedBy = mock(MappedBy.class);
        when(mappedBy.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mappedBy.typeConverter()).thenReturn(NoConversionTypeConverter.class.getName());
        when(mappedBy.field()).thenReturn(MappedBy.DEFAULT_FIELD);
        when(reflectionHelper.newInstance(NoConversionTypeConverter.class.getName())).
                thenThrow(new ClassCastException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Declared TypeConverter class org.jtransfo.NoConversionTypeConverter " +
                "cannot be cast to a TypeConverter.");
        converterHelper.getDeclaredTypeConverter(mappedBy);
    }

    @Test
    public void testGetDefaultTypeConverter() throws Exception {

        TypeConverter typeConverter = converterHelper.getDefaultTypeConverter(Object.class, Date.class);

        assertThat(typeConverter).isInstanceOf(NoConversionTypeConverter.class);
    }

    @Test
    public void testGetToConverter() throws Exception {
        // @todo
    }

}

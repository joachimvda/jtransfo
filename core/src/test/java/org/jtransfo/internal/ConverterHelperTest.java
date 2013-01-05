/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.MappedBy;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.ToConverter;
import org.jtransfo.TypeConverter;
import org.jtransfo.object.FaultyExtendedTo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        Field[] res;
        res = converterHelper.findField(fields, "c", new String[0]);
        assertThat(res[0]).isEqualTo(f2);

        res = converterHelper.findField(fields, "bla", new String[0]);
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
    public void testGetDefaultTypeConverterEmptyList() throws Exception {

        TypeConverter typeConverter = converterHelper.getDefaultTypeConverter(Object.class, Date.class);

        assertThat(typeConverter).isInstanceOf(NoConversionTypeConverter.class);
    }

    @Test
    public void testGetDefaultTypeConverterMatchingConverter() throws Exception {
        TypeConverter typeConverter = mock(TypeConverter.class);
        when(typeConverter.canConvert(any(Class.class), any(Class.class))).thenReturn(true);
        ReflectionTestUtils.setField(converterHelper, "typeConvertersInOrder",
                Collections.singletonList(typeConverter));

        TypeConverter result = converterHelper.getDefaultTypeConverter(Object.class, Date.class);

        assertThat(result).isEqualTo(typeConverter);
    }

    @Test
    public void testGetDefaultTypeConverterNoMatchingConverter() throws Exception {
        List<TypeConverter> tcs = new ArrayList<TypeConverter>();
        tcs.add(new DefaultTypeConverter());
        tcs.add(new DefaultTypeConverter());
        ReflectionTestUtils.setField(converterHelper, "typeConvertersInOrder", tcs);

        TypeConverter typeConverter = converterHelper.getDefaultTypeConverter(Object.class, Date.class);

        assertThat(typeConverter).isInstanceOf(NoConversionTypeConverter.class);
    }

    @Test
    public void testGetToConverter() throws Exception {
        ReflectionTestUtils.setField(converterHelper, "reflectionHelper", new ReflectionHelper()); // echte gebruiken

        ToConverter res = converterHelper.getToConverter(SimpleExtendedTo.class, SimpleExtendedDomain.class);

        assertThat(res).isNotNull();
        assertThat(res.getToTo()).hasSize(4);
        assertThat(res.getToDomain()).hasSize(3);
    }

    @Test
    public void testGetToConverterInvalidMapping() throws Exception {
        ReflectionTestUtils.setField(converterHelper, "reflectionHelper", new ReflectionHelper()); // echte gebruiken

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot determine mapping for field string in class " +
                "org.jtransfo.object.FaultyExtendedTo. The field zzz in class " +
                "org.jtransfo.object.SimpleExtendedDomain cannot be found.");

        converterHelper.getToConverter(FaultyExtendedTo.class, SimpleExtendedDomain.class);
    }

    @Test
    public void testSetTypeConvertersInOrder() throws Exception {
        List org = (List) ReflectionTestUtils.getField(converterHelper, "typeConvertersInOrder");
        TypeConverter typeConverter = mock(TypeConverter.class);
        assertThat(org.size()).isEqualTo(0); // default is empty

        converterHelper.setTypeConvertersInOrder(Collections.singletonList(typeConverter));
        List res = (List) ReflectionTestUtils.getField(converterHelper, "typeConvertersInOrder");
        assertThat(res.size()).isEqualTo(1);
        assertThat((Object) res).isInstanceOf(LockableList.class);
        assertThat((Boolean) ReflectionTestUtils.getField(res, "readOnly")).isTrue();
    }

    private class DefaultTypeConverter implements TypeConverter<Object, Object> {

        @Override
        public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
            return false;
        }

        @Override
        public Object convert(Object object, Class<Object> domainClass) throws JTransfoException {
            return null;
        }

        @Override
        public Object reverse(Object object, Class<Object> toClass) throws JTransfoException {
            return null;
        }
    }
}

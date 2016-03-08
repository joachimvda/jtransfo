/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.MapOnly;
import org.jtransfo.MappedBy;
import org.jtransfo.Named;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.ToConverter;
import org.jtransfo.TypeConverter;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.FaultyExtendedTo;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonTransitiveTo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.jtransfo.object.TaggedPersonTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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
        List<SyntheticField> fields = new ArrayList<SyntheticField>();
        SyntheticField f1 = new SimpleSyntheticField(SimpleExtendedDomain.class.getDeclaredField("b"));
        SyntheticField f2 = new SimpleSyntheticField(SimpleExtendedDomain.class.getDeclaredField("c"));
        fields.add(f1);
        fields.add(f2);

        SyntheticField[] res;
        res = converterHelper.findField(fields, "c", new String[0], SimpleExtendedDomain.class, false);
        assertThat(res[0]).isEqualTo(f2);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot find getter from [getBla, isBla, hasBla] on class " +
                "org.jtransfo.object.SimpleExtendedDomain.");

        converterHelper.findField(fields, "bla", new String[0], SimpleExtendedDomain.class, false);
    }

    @Test
    public void testFindFieldTransitive() throws Exception {
        List<SyntheticField> fields = new ArrayList<SyntheticField>();
        SyntheticField f1 = new SimpleSyntheticField(PersonDomain.class.getDeclaredField("name"));
        SyntheticField f2 = new SimpleSyntheticField(PersonDomain.class.getDeclaredField("address"));
        fields.add(f1);
        fields.add(f2);
        SyntheticField f3 = new SimpleSyntheticField(AddressDomain.class.getDeclaredField("id"));
        when(reflectionHelper.getSyntheticFields(any(Class.class))).thenReturn(Collections.singletonList(f3));

        SyntheticField[] res;
        res = converterHelper.findField(fields, "id", new String[] { "address" }, PersonDomain.class, false);
        assertThat(res).hasSize(2);
        assertThat(res[0]).isEqualTo(f2);
        assertThat(res[1]).isEqualTo(f3);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot find getter from [getBla, isBla, hasBla] on " +
                "class org.jtransfo.object.PersonDomain.");

        converterHelper.findField(fields, "id", new String[]{"bla"}, PersonDomain.class, false);
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
        assertThat(res.getToTo().get(0)).isInstanceOf(ToToConverter.class);
        assertThat(res.getToDomain()).hasSize(3);
        assertThat(res.getToDomain().get(0)).isInstanceOf(ToDomainConverter.class);
    }

    @Test
    public void testGetToConverter_mapOnly() throws Exception {
        ReflectionTestUtils.setField(converterHelper, "reflectionHelper", new ReflectionHelper()); // echte gebruiken
        ((Map<String, TypeConverter>) ReflectionTestUtils.getField(converterHelper, "typeConverterInstances")).
                put("always2", new DefaultTypeConverter());

        ToConverter res = converterHelper.getToConverter(TaggedPersonTo.class, PersonDomain.class);

        assertThat(res).isNotNull();
        assertThat(res.getToTo()).hasSize(4);
        assertThat(res.getToTo().get(0)).isInstanceOf(TaggedConverter.class);
        assertThat(res.getToDomain()).hasSize(3);
        assertThat(res.getToDomain().get(0)).isInstanceOf(TaggedConverter.class);
    }

    @Test
    public void testGetToConverterTransitive() throws Exception {
        ReflectionTestUtils.setField(converterHelper, "reflectionHelper", new ReflectionHelper()); // echte gebruiken

        ToConverter res = converterHelper.getToConverter(PersonTransitiveTo.class, PersonDomain.class);

        assertThat(res).isNotNull();
        assertThat(res.getToTo()).hasSize(3);
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

    @Test
    public void testSetTypeConvertersInOrderNamed() throws Exception {
        List org = (List) ReflectionTestUtils.getField(converterHelper, "typeConvertersInOrder");
        NamedTypeConverter typeConverter = mock(NamedTypeConverter.class);
        assertThat(org.size()).isEqualTo(0); // default is empty

        converterHelper.setTypeConvertersInOrder((Collection) Collections.singletonList(typeConverter));
        List res = (List) ReflectionTestUtils.getField(converterHelper, "typeConvertersInOrder");
        assertThat(res.size()).isEqualTo(1);
        assertThat((Object) res).isInstanceOf(LockableList.class);
        assertThat((Boolean) ReflectionTestUtils.getField(res, "readOnly")).isTrue();
        verify(typeConverter).getName();
    }

    @Test
    public void testWithPath() throws Exception {
        assertThat(converterHelper.withPath(new String[]{"parts", "to", "add"})).isEqualTo(" (with path parts.to.add) ");
    }

    @Test
    public void testGetDeclaredTypeConverter() throws Exception {
        MapOnly mapOnly = mock(MapOnly.class);
        when(mapOnly.typeConverterClass()).thenReturn(MappedBy.DefaultTypeConverter.class);
        when(mapOnly.typeConverter()).thenReturn(MappedBy.DEFAULT_TYPE_CONVERTER);
        TypeConverter tc = mock(TypeConverter.class);

        TypeConverter res = converterHelper.getDeclaredTypeConverter(mapOnly, tc);

        assertThat(res).isEqualTo(tc);
    }

    @Test
    public void testGetDeclaredTypeConverter_withTypeConverter() throws Exception {
        MapOnly mapOnly = mock(MapOnly.class);
        when(reflectionHelper.newInstance("org.jtransfo.internal.ConverterHelperTest$DefaultTypeConverter")).
                thenReturn(new DefaultTypeConverter());
        when(mapOnly.typeConverterClass()).thenReturn(DefaultTypeConverter.class);
        when(mapOnly.typeConverter()).thenReturn(MappedBy.DEFAULT_TYPE_CONVERTER);
        TypeConverter tc = mock(TypeConverter.class);

        TypeConverter res = converterHelper.getDeclaredTypeConverter(mapOnly, tc);

        assertThat(res).isInstanceOf(DefaultTypeConverter.class);
    }

    @Test
    public void testGetMapOnlies_both() throws Exception {
        Field field = TaggedPersonTo.class.getDeclaredField("gender"); // has both annotations
        field.setAccessible(true);

        List<MapOnly> res = converterHelper.getMapOnlies(field);

        assertThat(res).hasSize(2); // one MapOnly, one in MapOnlies
    }

    @Test
    public void testGetMapOnlies_mapOnlies() throws Exception {
        Field field = TaggedPersonTo.class.getDeclaredField("name"); // has both annotations
        field.setAccessible(true);

        List<MapOnly> res = converterHelper.getMapOnlies(field);

        assertThat(res).hasSize(2); // no MapOnly, two in MapOnlies
    }

    @Test
    public void testGetMapOnlies_none() throws Exception {
        Field field = TaggedPersonTo.class.getDeclaredField("lastChanged"); // has both annotations
        field.setAccessible(true);

        List<MapOnly> res = converterHelper.getMapOnlies(field);

        assertThat(res).isNull(); // neither -> null
    }

    @Test
    public void testGetDeclaredTypeConverter_null() throws Exception {
        TypeConverter tc = mock(TypeConverter.class);

        TypeConverter res = converterHelper.getDeclaredTypeConverter(null, tc);

        assertThat(res).isEqualTo(tc);
    }

    private class DefaultTypeConverter implements TypeConverter<Object, Object> {

        @Override
        public boolean canConvert(Type realToType, Type realDomainType) {
            return false;
        }

        @Override
        public Object convert(Object object, SyntheticField domainField, Object domainObject, String... tags)
                throws JTransfoException {
            return null;
        }

        @Override
        public Object reverse(Object object, SyntheticField toField, Object toObject, String... tags)
                throws JTransfoException {
            return null;
        }
    }

    private interface NamedTypeConverter extends TypeConverter, Named {
    }
}

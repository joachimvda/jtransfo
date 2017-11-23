/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoException;
import org.jtransfo.NeedsJTransfo;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.ObjectFinder;
import org.jtransfo.PostConverter;
import org.jtransfo.PreConverter;
import org.jtransfo.TypeConverter;
import org.jtransfo.object.PersonTo;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Test for JTransfoImpl.
 */
public class JTransfoImplTest {

    private JTransfoImpl jTransfo;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Mock
    private ConverterHelper converterHelper;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        jTransfo = new JTransfoImpl();

        ReflectionTestUtils.setField(((List) ReflectionTestUtils.getField(jTransfo, "internalObjectFinders")).get(0), "reflectionHelper", reflectionHelper);
    }

    @Test
    public void testConvertNull() {
        assertThat(jTransfo.convert(null)).isNull();
    }

    @Test
    public void testConvert2NullLeft() {
        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert("bla", null));

        assertThat(exc.getMessage()).isEqualTo("Source and target are required to be not-null.");
    }

    @Test
    public void testConvert2NullRight() {

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert(null, "bla"));

        assertThat(exc.getMessage()).isEqualTo("Source and target are required to be not-null.");
    }

    @Test
    public void testConvert2DomainClass() {

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert("alb", "bla"));

        assertThat(exc.getMessage()).isEqualTo("Neither source nor target are annotated with DomainClass on classes " +
                "java.lang.String and java.lang.String.");
    }

    @Test
    public void testConvertToNull() {
        assertThat(jTransfo.convertTo(null, this.getClass())).isNull();
    }

    @Test
    public void testFindTargetNull() {
        assertThat(jTransfo.findTarget(null, this.getClass())).isNull();
    }

    @Test
    public void testConvertInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert(new SimpleClassNameTo()));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testConvertToInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convertTo(new SimpleClassNameTo(), SimpleClassDomain.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testFindTargetInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.findTarget(new SimpleClassNameTo(), SimpleClassDomain.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testConvertIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert(new SimpleClassNameTo()));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testConvertToIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convertTo(new SimpleClassNameTo(), SimpleClassDomain.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testFindTargetIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.findTarget(new SimpleClassNameTo(), SimpleClassDomain.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
    }

    @Test
    public void testConvertNullTarget() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenReturn(null);

        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convert(new SimpleClassNameTo()));

        assertThat(exc.getMessage()).startsWith("Cannot create instance of target class org.jtransfo.object.SimpleClassDomain " +
                "for source object org.jtransfo.object.SimpleClassNameTo");
    }

    @Test
    public void testGetUpdateTypeConverters() throws Exception {
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);

        List<TypeConverter> converters = jTransfo.getTypeConverters();
        int orgSize = converters.size();
        int internalSize = ((List) ReflectionTestUtils.getField(jTransfo, "internalTypeConverters")).size();

        converters.add(new NoConversionTypeConverter());
        NeedsJTransfoTypeConverter needsJTransfoTypeConverter = mock(NeedsJTransfoTypeConverter.class);
        converters.add(needsJTransfoTypeConverter);
        verifyNoMoreInteractions(converterHelper);

        jTransfo.updateTypeConverters(); // update with changed converters

        verify(needsJTransfoTypeConverter).setJTransfo(jTransfo);
        ArgumentCaptor<Collection> captor1 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setTypeConvertersInOrder(captor1.capture());
        assertThat(captor1.getValue().size()).isEqualTo(orgSize + internalSize + 2);

        reset(converterHelper);
        jTransfo.updateTypeConverters(Collections.singletonList(new NoConversionTypeConverter()));

        ArgumentCaptor<Collection> captor2 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setTypeConvertersInOrder(captor2.capture());
        assertThat(captor2.getValue().size()).isEqualTo(internalSize + 1);
    }

    @Test
    public void testGetUpdatePreConverters() throws Exception {
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);

        List<PreConverter> converters = jTransfo.getPreConverters();
        int orgSize = converters.size();

        converters.add(mock(PreConverter.class));
        NeedsJTransfoPreConverter needsJTransfoPreConverter = mock(NeedsJTransfoPreConverter.class);
        converters.add(needsJTransfoPreConverter);
        verifyNoMoreInteractions(converterHelper);

        jTransfo.updatePreConverters(); // update with changed converters

        verify(needsJTransfoPreConverter).setJTransfo(jTransfo);
        ArgumentCaptor<Collection> captor1 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setPreConverters(captor1.capture());
        assertThat(captor1.getValue().size()).isEqualTo(orgSize + 2);

        reset(converterHelper);
        jTransfo.updatePreConverters(Collections.singletonList(mock(PreConverter.class)));

        ArgumentCaptor<Collection> captor2 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setPreConverters(captor2.capture());
        assertThat(captor2.getValue().size()).isEqualTo(1);
    }

    @Test
    public void testGetUpdatePostConverters() throws Exception {
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);

        List<PostConverter> converters = jTransfo.getPostConverters();
        int orgSize = converters.size();

        converters.add(mock(PostConverter.class));
        NeedsJTransfoPostConverter needsJTransfoPostConverter = mock(NeedsJTransfoPostConverter.class);
        converters.add(needsJTransfoPostConverter);
        verifyNoMoreInteractions(converterHelper);

        jTransfo.updatePostConverters(); // update with changed converters

        verify(needsJTransfoPostConverter).setJTransfo(jTransfo);
        ArgumentCaptor<Collection> captor1 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setPostConverters(captor1.capture());
        assertThat(captor1.getValue().size()).isEqualTo(orgSize + 2);

        reset(converterHelper);
        jTransfo.updatePostConverters(Collections.singletonList(mock(PostConverter.class)));

        ArgumentCaptor<Collection> captor2 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setPostConverters(captor2.capture());
        assertThat(captor2.getValue().size()).isEqualTo(1);
    }

    @Test
    public void testGetUpdateObjectFinders() throws Exception {
        List<ObjectFinder> internalObjectFinders;
        List<ObjectFinder> objectFinders = jTransfo.getObjectFinders();
        int orgSize = objectFinders.size();
        int internalObjectSize = ((List) ReflectionTestUtils.getField(jTransfo, "internalObjectFinders")).size();

        objectFinders.add(new NewInstanceObjectFinder());

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(orgSize + internalObjectSize);

        jTransfo.updateObjectFinders(); // update with changed converters

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(orgSize + internalObjectSize + 1);

        jTransfo.updateObjectFinders((List) Collections.singletonList(new NewInstanceObjectFinder()));

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(1 + internalObjectSize);
    }

    @Test
    public void testGetUpdateConvertInterceptors() throws Exception {
        ConvertSourceTarget convertInterceptorChain;
        List<ConvertInterceptor> convertInterceptors = jTransfo.getConvertInterceptors();

        convertInterceptorChain = (ConvertSourceTarget) ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain");

        convertInterceptors.add(new ConvertInterceptor() {
            @Override
            public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next,
                    String... tags) {
                return next.convert(source, target, isTargetTo, tags);
            }
        });

        // no change yet
        assertThat(ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain")).
                isEqualTo(convertInterceptorChain);

        jTransfo.updateConvertInterceptors(); // update with changed converters

        // changed now
        assertThat(ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain")).
                isNotEqualTo(convertInterceptorChain);
    }

    @Test
    public void testGetUpdateConvertInterceptors_newList() throws Exception {
        ConvertSourceTarget convertInterceptorChain;
        List<ConvertInterceptor> convertInterceptors = new ArrayList<>();

        convertInterceptorChain = (ConvertSourceTarget) ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain");

        convertInterceptors.add(new ConvertInterceptor() {
            @Override
            public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next,
                    String... tags) {
                return next.convert(source, target, isTargetTo, tags);
            }
        });

        // no change yet
        assertThat(ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain")).
                isEqualTo(convertInterceptorChain);

        jTransfo.updateConvertInterceptors(convertInterceptors); // update with changed converters

        // changed now
        assertThat(ReflectionTestUtils.getField(jTransfo, "convertInterceptorChain")).
                isNotEqualTo(convertInterceptorChain);
    }

    @Test
    public void testFindTarget() throws Exception {
        SimpleClassNameTo to = new SimpleClassNameTo();
        SimpleClassDomain target = mock(SimpleClassDomain.class);
        ObjectFinder objectFinder = mock(ObjectFinder.class);
        when(objectFinder.getObject(SimpleClassDomain.class, to)).thenReturn(target);

        jTransfo.updateObjectFinders(Collections.singletonList(objectFinder));

        SimpleClassDomain res = jTransfo.findTarget(to, SimpleClassDomain.class);

        verify(objectFinder).getObject(SimpleClassDomain.class, to);
        assertThat(res).isEqualTo(target);
    }

    @Test
    public void isToClassTest() throws Exception {
        assertThat(jTransfo.isToClass(SimpleClassNameTo.class)).isTrue();
        assertThat(jTransfo.isToClass(SimpleClassTypeTo.class)).isTrue();
        assertThat(jTransfo.isToClass(SimpleClassDomain.class)).isFalse();
    }

    @Test
    public void testConvertList() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassNameTo.class)).thenReturn(new SimpleClassNameTo());
        List<SimpleClassDomain> list = new ArrayList<>();
        list.add(new SimpleClassDomain());
        list.add(new SimpleClassDomain());

        List<SimpleClassNameTo> res = jTransfo.convertList(list, SimpleClassNameTo.class);

        assertThat(res).isNotNull().isNotEmpty().hasSize(2);
        assertThat(res.get(0)).isInstanceOf(SimpleClassNameTo.class);
    }

    @Test
    public void testConvertListNull() {
        assertThat(jTransfo.convertList(null, SimpleClassNameTo.class)).isNull();
    }

    @Test
    public void testConvert_passTags() throws Exception {
        ConvertSourceTarget cst = mock(ConvertSourceTarget.class);
        ReflectionTestUtils.setField(jTransfo, "convertInterceptorChain", cst);
        Object source = new PersonTo(); // real object, need annotation
        Object target = mock(Object.class);
        String[] tags = new String[] { "bla" };

        jTransfo.convert(source, target, tags);

        verify(cst).convert(source, target, false, tags);
    }

    @Test
    public void testConvert_noTags() throws Exception {
        ConvertSourceTarget cst = mock(ConvertSourceTarget.class);
        ReflectionTestUtils.setField(jTransfo, "convertInterceptorChain", cst);
        Object source = new PersonTo(); // real object, need annotation
        Object target = mock(Object.class);

        jTransfo.convert(source, target);

        verify(cst).convert(source, target, false, JTransfo.DEFAULT_TAG_WHEN_NO_TAGS);
    }

    @Test
    public void clearCaches() throws Exception {
        jTransfo.clearCaches();
        // just to check it does not fail
    }

    private interface NeedsJTransfoTypeConverter extends TypeConverter, NeedsJTransfo {
    }

    private interface NeedsJTransfoPreConverter extends PreConverter, NeedsJTransfo {
    }

    private interface NeedsJTransfoPostConverter extends PostConverter, NeedsJTransfo {
    }
}

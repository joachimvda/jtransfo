/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.internal.NewInstanceObjectFinder;
import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
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

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        jTransfo = new JTransfoImpl();

        ReflectionTestUtils.setField((jTransfo).getObjectFinders().get(0), "reflectionHelper", reflectionHelper);
    }

    @Test
    public void testConvertNull() {
        assertThat(jTransfo.convert(null)).isNull();
    }

    @Test
    public void testConvert2NullLeft() {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Source and target are required to be not-null.");

        jTransfo.convert("bla", null);
    }

    @Test
    public void testConvert2NullRight() {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Source and target are required to be not-null.");

        jTransfo.convert(null, "bla");
    }

    @Test
    public void testConvert2DomainClass() {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Neither source nor target are annotated with DomainClass on classes " +
                "java.lang.String and java.lang.String.");

        jTransfo.convert("alb", "bla");
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

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testConvertToInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convertTo(new SimpleClassNameTo(), SimpleClassDomain.class);
    }

    @Test
    public void testFindTargetInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.findTarget(new SimpleClassNameTo(), SimpleClassDomain.class);
    }

    @Test
    public void testConvertIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testConvertToIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convertTo(new SimpleClassNameTo(), SimpleClassDomain.class);
    }

    @Test
    public void testFindTargetIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.findTarget(new SimpleClassNameTo(), SimpleClassDomain.class);
    }

    @Test
    public void testConvertNullTarget() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenReturn(null);
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of target class org.jtransfo.object.SimpleClassDomain " +
                "for source object org.jtransfo.object.SimpleClassNameTo");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testGetUpdateTypeConverters() throws Exception {
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);

        List<TypeConverter> converters = jTransfo.getTypeConverters();
        int orgSize = converters.size();

        converters.add(new NoConversionTypeConverter());
        NeedsJTransfoTypeConverter needsJTransfoTypeConverter = mock(NeedsJTransfoTypeConverter.class);
        converters.add(needsJTransfoTypeConverter);
        verifyNoMoreInteractions(converterHelper);

        jTransfo.updateTypeConverters(); // update with changed converters

        verify(needsJTransfoTypeConverter).setJTransfo(jTransfo);
        ArgumentCaptor<Collection> captor1 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setTypeConvertersInOrder(captor1.capture());
        assertThat(captor1.getValue().size()).isEqualTo(orgSize + 2);

        reset(converterHelper);
        jTransfo.updateTypeConverters((List) Collections.singletonList(new NoConversionTypeConverter()));

        ArgumentCaptor<Collection> captor2 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setTypeConvertersInOrder(captor2.capture());
        assertThat(captor2.getValue().size()).isEqualTo(1);
    }

    @Test
    public void testGetUpdateObjectFinders() throws Exception {
        List<ObjectFinder> internalObjectFinders;
        List<ObjectFinder> objectFinders = jTransfo.getObjectFinders();
        int orgSize = objectFinders.size();

        objectFinders.add(new NewInstanceObjectFinder());

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(orgSize);

        jTransfo.updateObjectFinders(); // update with changed converters

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(orgSize + 1);

        jTransfo.updateObjectFinders((List) Collections.singletonList(new NewInstanceObjectFinder()));

        internalObjectFinders = (List<ObjectFinder>) ReflectionTestUtils.getField(jTransfo, "objectFinders");
        assertThat(internalObjectFinders).hasSize(1);
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
        List<ConvertInterceptor> convertInterceptors = new ArrayList<ConvertInterceptor>();

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
        List<SimpleClassDomain> list = new ArrayList<SimpleClassDomain>();
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

    private interface NeedsJTransfoTypeConverter extends TypeConverter, NeedsJTransfo {
    }
}

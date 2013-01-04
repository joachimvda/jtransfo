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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
    public void testConvertInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testConvertIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testConvertNullTarget() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenReturn(null);
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of domain class org.jtransfo.object.SimpleClassDomain " +
                "for transfer object org.jtransfo.object.SimpleClassNameTo");
        jTransfo.convert(new SimpleClassNameTo());
    }

    @Test
    public void testGetUpdateTypeConverters() throws Exception {
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);

        List<TypeConverter> converters = jTransfo.getTypeConverters();
        int orgSize = converters.size();

        converters.add(new NoConversionTypeConverter());
        verifyNoMoreInteractions(converterHelper);

        jTransfo.updateTypeConverters(); // update with changed converters

        ArgumentCaptor<Collection> captor1 = ArgumentCaptor.forClass(Collection.class);
        verify(converterHelper, times(1)).setTypeConvertersInOrder(captor1.capture());
        assertThat(captor1.getValue().size()).isEqualTo(orgSize + 1);

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
}

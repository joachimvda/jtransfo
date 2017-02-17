/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.Converter;
import org.jtransfo.MapOnly;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.MapEntry.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Test for {@link TaggedConverter}.
 */
public class TaggedConverterTest {

    private static final String TAG = "key";

    private TaggedConverter taggedConverter;

    @Before
    public void setUp() throws Exception {
        taggedConverter = new TaggedConverter();
    }

    @Test
    public void testAddConverters() throws Exception {
        Converter c1 = mock(Converter.class);
        Converter c2 = mock(Converter.class);
        taggedConverter.addConverters(c1, TAG, "zzz");
        taggedConverter.addConverters(c2, TAG);

        Map<String, Converter> res = (Map<String, Converter>)
                ReflectionTestUtils.getField(taggedConverter, "converters");

        assertThat(res).hasSize(2).contains(entry("zzz", c1), entry("key", c2));
    }

    @Test
    public void testConvert() throws Exception {
        Converter starConverter = mock(Converter.class);
        Converter converter = mock(Converter.class);
        taggedConverter.addConverters(converter, TAG);
        taggedConverter.addConverters(starConverter, MapOnly.ALWAYS);
        Object source = mock(Object.class);
        Object target = mock(Object.class);

        taggedConverter.convert(source, target);

        verifyNoMoreInteractions(converter);
        verify(starConverter).convert(source, target);
    }

    @Test
    public void testConvert_withTags() throws Exception {
        Converter converter = mock(Converter.class);
        taggedConverter.addConverters(converter, TAG);
        Object source = mock(Object.class);
        Object target = mock(Object.class);

        taggedConverter.convert(source, target, TAG);

        verify(converter).convert(source, target, TAG);
    }

    @Test
    public void testConvert_withoutTags() throws Exception {
        Converter converter = mock(Converter.class);
        taggedConverter.addConverters(converter, TAG);
        Object source = mock(Object.class);
        Object target = mock(Object.class);

        taggedConverter.convert(source, target, "bla");

        verifyNoMoreInteractions(converter);
    }

    @Test
    public void testConvert_withNotTags() throws Exception {
        Converter converter = mock(Converter.class);
        taggedConverter.addConverters(converter, "!" + TAG);
        Object source = mock(Object.class);
        Object target = mock(Object.class);

        taggedConverter.convert(source, target, TAG);

        verifyNoMoreInteractions(converter);
    }

    @Test
    public void testConvert_withoutNotTags() throws Exception {
        Converter converter = mock(Converter.class);
        taggedConverter.addConverters(converter, "!" + TAG);
        Object source = mock(Object.class);
        Object target = mock(Object.class);

        taggedConverter.convert(source, target, "bla");

        verify(converter).convert(source, target, "bla");
    }
}

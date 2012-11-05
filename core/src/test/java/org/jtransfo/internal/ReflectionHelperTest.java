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

import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

public class ReflectionHelperTest {

    private static final String REFLECTION_HELPER_CLASS = "org.jtransfo.internal.ReflectionHelper";

    private ReflectionHelper reflectionHelper;

    @Before
    public void setUp() throws Exception {
        reflectionHelper = new ReflectionHelper();
    }

    @Test
    public void testNewInstanceClass() throws Exception {
        assertThat(reflectionHelper.newInstance(ReflectionHelper.class)).isInstanceOf(ReflectionHelper.class);
    }

    @Test
    public void testNewInstanceName() throws Exception {
        assertThat(reflectionHelper.newInstance(REFLECTION_HELPER_CLASS)).isInstanceOf(ReflectionHelper.class);
    }

    @Test
    public void testLoadClass() throws Exception {
        Class reflectionHelperClass = reflectionHelper.loadClass(REFLECTION_HELPER_CLASS);
        assertThat(reflectionHelperClass.getName()).isEqualTo(REFLECTION_HELPER_CLASS);
    }

    @Test
    public void testGetFields() throws Exception {
        List<Field> fields = reflectionHelper.getFields(SimpleExtendedDomain.class);
        assertThat(fields).hasSize(4).
                onProperty("name").contains("a", "b", "c", "i");
    }

    @Test
    public void testMakeAccessible() throws Exception {
        Field field = SimpleClassDomain.class.getDeclaredField("bla");
        assertThat(field.isAccessible()).isFalse(); // is private
        reflectionHelper.makeAccessible(field);
        assertThat(field.isAccessible()).isTrue();
    }
}

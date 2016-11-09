/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.object.SimpleBaseTo;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        Object instance = reflectionHelper.newInstance(REFLECTION_HELPER_CLASS);
        assertThat(instance).isInstanceOf(ReflectionHelper.class);
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
                extracting("name").contains("a", "b", "c", "i");
    }

    @Test
    public void testMakeAccessible() throws Exception {
        Field field = SimpleClassDomain.class.getDeclaredField("bla");
        assertThat(field.isAccessible()).isFalse(); // is private
        reflectionHelper.makeAccessible(field);
        assertThat(field.isAccessible()).isTrue();
    }

    @Test
    public void testMakeSynthetic() throws Exception {
        List<Field> orgFields = new ArrayList<>();
        orgFields.add(SimpleExtendedDomain.class.getDeclaredField("b"));
        orgFields.add(SimpleExtendedDomain.class.getDeclaredField("c"));
        List<SyntheticField> fields = reflectionHelper.makeSynthetic(SimpleExtendedDomain.class, orgFields);
        assertThat(fields).hasSize(2);
        for (SyntheticField sf : fields) {
            assertThat(sf).isInstanceOf(AccessorSyntheticField.class);
        }
    }

    @Test
    public void testGetSyntheticFields() throws Exception {
        List<SyntheticField> fields = reflectionHelper.getSyntheticFields(SimpleExtendedDomain.class);
        assertThat(fields).hasSize(4).
                extracting("name").contains("a", "b", "c", "i");
    }

    @Test
    public void testGetMethod() throws Exception {
        Method method;
        method = reflectionHelper.getMethod(SimpleBaseTo.class, null, "setA", String.class);
        assertThat(method.getName()).isEqualTo("setA");

        method = reflectionHelper.getMethod(SimpleBaseTo.class, null, "setA", Integer.class);
        assertThat(method).isNull();

        method = reflectionHelper.getMethod(SimpleBaseTo.class, String.class, "getA");
        assertThat(method.getName()).isEqualTo("getA");

        method = reflectionHelper.getMethod(SimpleBaseTo.class, Integer.class, "getA");
        assertThat(method).isNull();

        method = reflectionHelper.getMethod(SimpleBaseTo.class, Object.class, "getA");
        assertThat(method.getName()).isEqualTo("getA");

        method = reflectionHelper.getMethod(SimpleBaseTo.class, null, "bla");
        assertThat(method).isNull();
    }

}

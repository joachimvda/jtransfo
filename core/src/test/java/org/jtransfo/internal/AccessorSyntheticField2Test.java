/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.object.ReadOnlyGetterDomain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Test for AccessorSyntheticField.
 */
public class AccessorSyntheticField2Test {

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(reflectionHelper.getMethod(ReadOnlyGetterDomain.class, null, "getId")).thenReturn(
                ReadOnlyGetterDomain.class.getMethod("getId"));
        when(reflectionHelper.getMethod(ReadOnlyGetterDomain.class, null, "setId", String.class)).thenReturn(
                ReadOnlyGetterDomain.class.getMethod("setId", String.class));
        when(reflectionHelper.getMethod(ReadOnlyGetterDomain.class, null, "getTwice")).thenReturn(
                ReadOnlyGetterDomain.class.getMethod("getTwice"));
    }

    @Test
    public void testConstructNoField() throws Exception {
        AccessorSyntheticField accessorSyntheticField =
                new AccessorSyntheticField(reflectionHelper, ReadOnlyGetterDomain.class, "id", false);

        assertThat(accessorSyntheticField.getName()).isEqualTo("id");
        assertThat(accessorSyntheticField.getType()).isEqualTo(String.class);

        ReadOnlyGetterDomain domain = new ReadOnlyGetterDomain();
        accessorSyntheticField.set(domain, "zzz");
        assertThat(accessorSyntheticField.get(domain)).isEqualTo("zzz");
    }

    @Test
    public void testConstructNoAccessors() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot find getter getAhum on class org.jtransfo.object.ReadOnlyGetterDomain.");

        new AccessorSyntheticField(reflectionHelper, ReadOnlyGetterDomain.class, "ahum", false);
    }

    @Test
    public void testConstructNoSetter() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot find setter setTwice on class org.jtransfo.object.ReadOnlyGetterDomain.");

        new AccessorSyntheticField(reflectionHelper, ReadOnlyGetterDomain.class, "twice", false);
    }

    @Test
    public void testConstructNoFieldReadOnly() throws Exception {
        AccessorSyntheticField accessorSyntheticField =
                new AccessorSyntheticField(reflectionHelper, ReadOnlyGetterDomain.class, "twice", true);

        ReadOnlyGetterDomain domain = new ReadOnlyGetterDomain();
        domain.setId("ahum");
        assertThat(accessorSyntheticField.get(domain)).isEqualTo("ahumahum");
    }

}

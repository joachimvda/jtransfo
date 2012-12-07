/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.JTransfoImpl;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Mockito.when;

/**
 * Test for {@link NewInstanceObjectFinder}.
 */
public class NewInstanceObjectFinderTest {

    private NewInstanceObjectFinder newInstanceObjectFinder;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        newInstanceObjectFinder = new NewInstanceObjectFinder();

        ReflectionTestUtils.setField(newInstanceObjectFinder, "reflectionHelper", reflectionHelper);
    }

    @Test
    public void testInstantiationException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        newInstanceObjectFinder.getObject(SimpleClassDomain.class, new SimpleClassNameTo());
    }

    @Test
    public void testIllegalAccessException() throws Exception {
        when(reflectionHelper.newInstance(SimpleClassDomain.class)).thenThrow(new IllegalAccessException());
        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for domain class org.jtransfo.object.SimpleClassDomain.");
        newInstanceObjectFinder.getObject(SimpleClassDomain.class, new SimpleClassNameTo());
    }

}

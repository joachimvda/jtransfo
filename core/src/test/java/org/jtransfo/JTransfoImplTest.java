/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;
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

public class JTransfoImplTest {

    private JTransfo jTransfo;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        jTransfo = new JTransfoImpl();

        ReflectionTestUtils.setField(jTransfo, "reflectionHelper", reflectionHelper);
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
}

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

import org.jtransfo.JTransfoException;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.object.SimpleBaseDomain;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static org.fest.assertions.Assertions.assertThat;

public class ToDomainConverterTest {

    private static final String A_VALUE = "a value";

    private ToDomainConverter toDomainConverter;
    private ToDomainConverter toDomainConverterAccess;
    private ToDomainConverter toDomainConverterArgument;
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        reflectionHelper = new ReflectionHelper();
        Field a = SimpleBaseDomain.class.getDeclaredField("a");
        Field b = SimpleExtendedDomain.class.getDeclaredField("b");
        Field c = SimpleExtendedDomain.class.getDeclaredField("c");
        Field i = SimpleExtendedDomain.class.getDeclaredField("i");
        reflectionHelper.makeAccessible(a);
        reflectionHelper.makeAccessible(c);
        reflectionHelper.makeAccessible(i);

        toDomainConverter = new ToDomainConverter(a, c, new NoConversionTypeConverter());
        toDomainConverterAccess = new ToDomainConverter(a, b, new NoConversionTypeConverter());
        toDomainConverterArgument = new ToDomainConverter(a, i, new NoConversionTypeConverter());
    }

    @Test
    public void testConvert() throws Exception {
        SimpleExtendedDomain sed = new SimpleExtendedDomain();

        sed.setA(A_VALUE);
        toDomainConverter.convert(sed, sed);
        assertThat(sed.getC()).isEqualTo(A_VALUE);
    }

    @Test
    public void testConvertIllegalAccessException() throws Exception {
        SimpleExtendedDomain sed = new SimpleExtendedDomain();

        sed.setA(A_VALUE);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert field a to field b, field cannot be accessed.");
        toDomainConverterAccess.convert(sed, sed);
    }

    @Test
    public void testConvertIllegalArgumentException() throws Exception {
        SimpleExtendedDomain sed = new SimpleExtendedDomain();

        sed.setA(A_VALUE);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert field a to field i, field needs type conversion.");
        toDomainConverterArgument.convert(sed, sed);
    }

}

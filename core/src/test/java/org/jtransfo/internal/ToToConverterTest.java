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

import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.object.SimpleBaseDomain;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.fest.assertions.Assertions.assertThat;

public class ToToConverterTest {

    private static final String C_VALUE = "c value";

    private ToToConverter toToConverter;
    private ReflectionHelper reflectionHelper;

    @Before
    public void setUp() throws Exception {
        reflectionHelper = new ReflectionHelper();
        Field a = SimpleBaseDomain.class.getDeclaredField("a");
        Field c = SimpleExtendedDomain.class.getDeclaredField("c");
        reflectionHelper.makeAccessible(a);
        reflectionHelper.makeAccessible(c);

        toToConverter = new ToToConverter(a, c, new NoConversionTypeConverter());
    }

    @Test
    public void testConvert() throws Exception {
        SimpleExtendedDomain sed = new SimpleExtendedDomain();

        sed.setC(C_VALUE);
        toToConverter.convert(sed, sed);
        assertThat(sed.getA()).isEqualTo(C_VALUE);
    }

}

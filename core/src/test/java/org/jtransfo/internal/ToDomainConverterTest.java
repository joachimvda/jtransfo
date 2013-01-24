/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonTransitiveTo;
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
        SyntheticField sa = new SimpleSyntheticField(a);
        SyntheticField sb = new SimpleSyntheticField(b);
        SyntheticField sc = new SimpleSyntheticField(c);
        SyntheticField si = new SimpleSyntheticField(i);

        toDomainConverter = new ToDomainConverter(sa, new SyntheticField[]{ sc }, new NoConversionTypeConverter());
        toDomainConverterAccess = new ToDomainConverter(sa, new SyntheticField[]{ sb }, new NoConversionTypeConverter());
        toDomainConverterArgument = new ToDomainConverter(sa, new SyntheticField[]{ si }, new NoConversionTypeConverter());
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
        exception.expectMessage("Cannot convert TO field a to domain field b, field cannot be accessed.");
        toDomainConverterAccess.convert(sed, sed);
    }

    @Test
    public void testConvertIllegalArgumentException() throws Exception {
        SimpleExtendedDomain sed = new SimpleExtendedDomain();

        sed.setA(A_VALUE);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert TO field a to domain field i, field needs type conversion.");
        toDomainConverterArgument.convert(sed, sed);
    }

    @Test
    public void testTransitiveNullException() throws Exception {
        Field addressId = PersonTransitiveTo.class.getDeclaredField("addressId");
        Field address = PersonDomain.class.getDeclaredField("address");
        Field id = AddressDomain.class.getDeclaredField("id");
        reflectionHelper.makeAccessible(addressId);
        reflectionHelper.makeAccessible(address);
        reflectionHelper.makeAccessible(id);
        SyntheticField sAddress = new SimpleSyntheticField(address);
        SyntheticField sId = new SimpleSyntheticField(id);

        toDomainConverter = new ToDomainConverter(new SimpleSyntheticField(addressId),
                new SyntheticField[]{ sAddress, sId }, new NoConversionTypeConverter());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert TO field addressId to domain field id (with path address), " +
                "transitive field address in path is null.");
        toDomainConverter.convert(new PersonTransitiveTo(), new PersonDomain());
    }

}

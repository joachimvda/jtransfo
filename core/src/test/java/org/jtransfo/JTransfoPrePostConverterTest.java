/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.DeleteGenderPostConverter;
import org.jtransfo.object.DoubleNamePostConverter;
import org.jtransfo.object.Gender;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonWithInvalidPostConvertTo;
import org.jtransfo.object.PersonWithInvalidPreConvertTo;
import org.jtransfo.object.PersonWithMultiplePrePostTo;
import org.jtransfo.object.PersonWithPrePostTo;
import org.jtransfo.object.SkipMePreConverter;
import org.jtransfo.object.SkipOtherPreConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that the pre/post converters work.
 */
public class JTransfoPrePostConverterTest {

    private JTransfo jTransfo;

    @BeforeEach
    public void setUp() throws Exception {
        ConfigurableJTransfo impl = JTransfoFactory.get();
        jTransfo = impl;
        impl.with(new StringEnumTypeConverter<Gender>(Gender.class));

        impl.with(new SkipMePreConverter());
        impl.with(new SkipOtherPreConverter());
        impl.with(new DeleteGenderPostConverter());
        impl.with(new DoubleNamePostConverter());
    }

    @Test
    public void testToDomain_one() throws Exception {
        PersonWithPrePostTo to = new PersonWithPrePostTo();
        to.setName("Joe");
        to.setGender("MALE");

        PersonDomain res = jTransfo.convertTo(to, PersonDomain.class);

        assertThat(res.getName()).isEqualTo("Joe");
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToTo_one() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Joe");
        domain.setGender(Gender.MALE);

        PersonWithPrePostTo res = jTransfo.convertTo(domain, PersonWithPrePostTo.class);

        assertThat(res.getName()).isEqualTo("Joe");
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToDomain_one_skip() throws Exception {
        PersonWithPrePostTo to = new PersonWithPrePostTo();
        to.setName("Me");
        to.setGender("MALE");

        PersonDomain res = jTransfo.convertTo(to, PersonDomain.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToTo_one_skip() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Me");
        domain.setGender(Gender.MALE);

        PersonWithPrePostTo res = jTransfo.convertTo(domain, PersonWithPrePostTo.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToDomain_multiple() throws Exception {
        PersonWithMultiplePrePostTo to = new PersonWithMultiplePrePostTo();
        to.setName("Joe");
        to.setGender("MALE");

        PersonDomain res = jTransfo.convertTo(to, PersonDomain.class);

        assertThat(res.getName()).isEqualTo("JoeJoe");
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToTo_multiple() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Joe");
        domain.setGender(Gender.MALE);

        PersonWithMultiplePrePostTo res = jTransfo.convertTo(domain, PersonWithMultiplePrePostTo.class);

        assertThat(res.getName()).isEqualTo("JoeJoe");
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToDomain_multiple_skip1() throws Exception {
        PersonWithMultiplePrePostTo to = new PersonWithMultiplePrePostTo();
        to.setName("Me");
        to.setGender("MALE");

        PersonDomain res = jTransfo.convertTo(to, PersonDomain.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToDomain_multiple_skip2() throws Exception {
        PersonWithMultiplePrePostTo to = new PersonWithMultiplePrePostTo();
        to.setName("Other");
        to.setGender("MALE");

        PersonDomain res = jTransfo.convertTo(to, PersonDomain.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToTo_multiple_skip1() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Me");
        domain.setGender(Gender.MALE);

        PersonWithMultiplePrePostTo res = jTransfo.convertTo(domain, PersonWithMultiplePrePostTo.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testToTo_multiple_skip2() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Other");
        domain.setGender(Gender.MALE);

        PersonWithMultiplePrePostTo res = jTransfo.convertTo(domain, PersonWithMultiplePrePostTo.class);

        assertThat(res.getName()).isNull();
        assertThat(res.getGender()).isNull();
    }

    @Test
    public void testinvalidPreConverter() throws Exception {
        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convertTo(new PersonDomain(), PersonWithInvalidPreConvertTo.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot find preConverter invalid.");
    }

    @Test
    public void testinvalidPostConverter() throws Exception {
        JTransfoException exc = Assertions.assertThrows(JTransfoException.class, () ->
                jTransfo.convertTo(new PersonDomain(), PersonWithInvalidPostConvertTo.class));

        assertThat(exc.getMessage()).isEqualTo("Cannot find postConverter invalid.");
    }

}

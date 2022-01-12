/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.SimpleWithEnumDomain;
import org.jtransfo.object.SimpleWithEnumTo;
import org.jtransfo.object.SomeEnum;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JTransfoWithEnumTest {

    private ConfigurableJTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        jTransfo = JTransfoFactory.get();
        jTransfo.with(new AutomaticStringEnumTypeConverter());
    }

    @Test
    public void testToTo() throws Exception {
        SimpleWithEnumDomain domain = new SimpleWithEnumDomain();
        domain.setA("aaa");
        domain.setB("bb");
        domain.setC("cccc");
        domain.setI(111);
        domain.setSome(SomeEnum.OTHER);

        SimpleWithEnumTo res = new SimpleWithEnumTo();
        jTransfo.convert(domain, res);

        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getString()).isEqualTo("bb");
        assertThat(res.getC()).isEqualTo("cccc");
        assertThat(res.getI()).isEqualTo(111);
        assertThat(res.getJ()).isEqualTo(0);
        assertThat(res.getK()).isEqualTo(0);
        assertThat(res.getSome()).isEqualTo("OTHER");
        assertThat(res.getSomeDescription()).isEqualTo("or other");
    }

    @Test
    public void testToDomain() throws Exception {
        SimpleWithEnumTo to = new SimpleWithEnumTo();
        to.setA("aaa");
        to.setString("bb");
        to.setC("cccc");
        to.setI(111);
        to.setJ(22);
        to.setK(3333);
        to.setSome("SOME");

        SimpleWithEnumDomain res = jTransfo.convertTo(to, SimpleWithEnumDomain.class);
        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getB()).isEqualTo("bb");
        assertThat(res.getC()).isNull(); // read-only in to
        assertThat(res.getI()).isEqualTo(111);
        assertThat(res.getSome()).isEqualTo(SomeEnum.SOME);
    }

}

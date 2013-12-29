/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jtransfo.JTransfo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import static org.fest.assertions.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JTransfoCdiDefaultTest {

    @Inject
    private JTransfo jTransfo;

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.jtransfo.cdi")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }

    @Test
    public void testToTo() throws Exception {
        SimpleExtendedDomain domain = new SimpleExtendedDomain();
        domain.setA("aaa");
        domain.setB("bb");
        domain.setC("cccc");
        domain.setI(111);

        SimpleExtendedTo res = new SimpleExtendedTo();
        jTransfo.convert(domain, res);

        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getString()).isEqualTo("bb");
        assertThat(res.getC()).isEqualTo("cccc");
        assertThat(res.getI()).isEqualTo(111);
        assertThat(res.getJ()).isEqualTo(0);
        assertThat(res.getK()).isEqualTo(0);
    }

    @Test
    public void testToDomain() throws Exception {
        SimpleExtendedTo to = new SimpleExtendedTo();
        to.setA("aaa");
        to.setString("bb");
        to.setC("cccc");
        to.setI(111);
        to.setJ(22);
        to.setK(3333);

        SimpleExtendedDomain res = (SimpleExtendedDomain) jTransfo.convert(to);
        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getB()).isEqualTo("bb");
        assertThat(res.getC()).isNull(); // read-only in to
        assertThat(res.getI()).isEqualTo(111);
    }

}

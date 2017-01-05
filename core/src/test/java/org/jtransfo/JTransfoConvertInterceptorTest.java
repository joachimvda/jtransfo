/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that the convert interceptors work.
 */
public class JTransfoConvertInterceptorTest {

    private JTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        ConfigurableJTransfo impl = JTransfoFactory.get();
        jTransfo = impl;

        impl.with(new MyConvertInterceptor("X"));
        impl.with(new MyConvertInterceptor("z"));
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
        assertThat(res.getA()).isEqualTo("aaazX"); // XZ added by interceptors
        assertThat(res.getB()).isEqualTo("bb");
        assertThat(res.getC()).isNull(); // read-only in to
        assertThat(res.getI()).isEqualTo(111);
    }

    private class MyConvertInterceptor implements ConvertInterceptor {
        private String adder;

        private MyConvertInterceptor(String adder) {
            this.adder = adder;
        }

        @Override
        public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
            T res = next.convert(source, target, isTargetTo, tags);
            if (res instanceof SimpleExtendedDomain) {
                SimpleExtendedDomain sed = (SimpleExtendedDomain) res;
                try {
                    sed.setA(sed.getA() + adder);
                } catch (IOException ioe) {
                    System.out.println("Unexpected " + ioe);
                }
            }
            return res;
        }
    }
}

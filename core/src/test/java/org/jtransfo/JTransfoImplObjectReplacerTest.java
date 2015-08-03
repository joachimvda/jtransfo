/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.SimpleBaseDomain;
import org.jtransfo.object.SimpleBaseTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that the convert interceptors work.
 */
public class JTransfoImplObjectReplacerTest {

    int determinatorCounter;

    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        JTransfoImpl impl = new JTransfoImpl();
        jTransfo = impl;

        impl.getObjectReplacers().add(new MyObjectReplacer());
        impl.updateObjectReplacers();

        determinatorCounter = 0;
        assertThat(determinatorCounter).isEqualTo(0);
    }

    @Test
    public void testToToNoDelegate() throws Exception {
        final SimpleBaseDomain domain = new SimpleBaseDomain();
        domain.setA("aaa");
        domain.setB("bb");

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert domain field");

        jTransfo.convertTo(new DelegateTwo(domain), SimpleBaseTo.class);
    }

    @Test
    public void testToToWithDelegate() throws Exception {
        final SimpleBaseDomain domain = new SimpleBaseDomain();
        domain.setA("aaa");
        domain.setB("bb");

        SimpleBaseTo res = jTransfo.convertTo(new DelegateOne(domain), SimpleBaseTo.class);

        assertThat(res).isNotNull();
        assertThat(res.getA()).isEqualTo("aaa"); // XZ added by interceptors
        assertThat(determinatorCounter).isEqualTo(0);
    }


    private class MyObjectReplacer implements ObjectReplacer {

        @Override
        public Object replaceObject(Object object) {
            if (object instanceof DelegateOne) {
                return ((DelegateOne) object).delegate;
            }
            return object;
        }
    }

    private class DelegateOne {
        private SimpleBaseDomain delegate;

        public DelegateOne(SimpleBaseDomain delegate) {
            this.delegate = delegate;
        }

        public String getA() throws IOException, JTransfoException, IllegalStateException {
            return delegate.getA();
        }

        public void setB(String b) {
            delegate.setB(b);
        }

        public String getB() {
            return delegate.getB();
        }

        public void setA(String a) throws IOException, JTransfoException, IllegalStateException {
            delegate.setA(a);
        }
    }

    private class DelegateTwo {
        private SimpleBaseDomain delegate;

        public DelegateTwo(SimpleBaseDomain delegate) {
            this.delegate = delegate;
        }

        public String getA() throws IOException, JTransfoException, IllegalStateException {
            return delegate.getA();
        }

        public void setB(String b) {
            delegate.setB(b);
        }

        public String getB() {
            return delegate.getB();
        }

        public void setA(String a) throws IOException, JTransfoException, IllegalStateException {
            delegate.setA(a);
        }
    }

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.FromInterfaceDomain;
import org.jtransfo.object.FromInterfaceDomainInterface;
import org.jtransfo.object.FromInterfaceTo;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that the convert interceptors work.
 */
public class JTransfoImplObjectClassDeterminatorTest {

    int determinatorCounter;

    private JTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        JTransfoImpl impl = new JTransfoImpl();
        jTransfo = impl;

        impl.getObjectClassDeterminators().add(new MyObjectClassDeterminator());
        impl.updateObjectClassDeterminators();

        determinatorCounter = 0;
        assertThat(determinatorCounter).isEqualTo(0);
    }

    @Test
    public void testToToNoProxy() throws Exception {
        final FromInterfaceDomain domain = new FromInterfaceDomain();
        domain.setA("aaa");
        domain.setB("bb");

        FromInterfaceTo res = jTransfo.convertTo(domain, FromInterfaceTo.class);

        assertThat(res).isNotNull();
        assertThat(res.getA()).isEqualTo("aaa"); // XZ added by interceptors
        assertThat(determinatorCounter).isEqualTo(0);
    }

    @Test
    public void testToToWithProxy() throws Exception {
        final FromInterfaceDomain domain = new FromInterfaceDomain();
        domain.setA("aaa");
        domain.setB("bb");

        FromInterfaceTo res = jTransfo.convertTo(Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{DummyMarker.class, FromInterfaceDomainInterface.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return method.invoke(domain, args);
                    }
                }), FromInterfaceTo.class);

        assertThat(res).isNotNull();
        assertThat(res.getA()).isEqualTo("aaa"); // XZ added by interceptors
        assertThat(determinatorCounter).isGreaterThan(0);
    }

    private class MyObjectClassDeterminator implements ObjectClassDeterminator {

        @Override
        public Class getObjectClass(Object object) {
            System.out.println(object.getClass().getName());
            if (object instanceof Proxy) {
                determinatorCounter++;
                System.out.println("+++converting");
                return FromInterfaceDomainInterface.class;
            }
            return null;
        }
    }

    private interface DummyMarker {}

}

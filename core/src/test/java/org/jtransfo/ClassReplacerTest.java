/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.PersonTo;
import org.jtransfo.object.PersonWithAgeDomain;
import org.jtransfo.object.PersonWithAgeDomainImpl;
import org.jtransfo.object.PersonWithAgeTo;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for the classReplacer mechanism.
 */
public class ClassReplacerTest {

    private static final String NAME = "ikke";

    private JTransfo jTransfo;

    @Before
    public void setup() {
        ConfigurableJTransfo impl = JTransfoFactory.get();
        impl.with(new StoriedHouseClassReplacer());
        jTransfo = impl;
    }

    @Test
    public void testReplaceClassToDomain() {
        PersonWithAgeTo to = new PersonWithAgeTo();
        to.setName("Joske");

        PersonWithAgeDomain res = (PersonWithAgeDomain) jTransfo.convert(to);

        assertThat(res).isInstanceOf(PersonWithAgeDomainImpl.class);
        assertThat(res.getName()).isEqualTo("Joske");

        res = jTransfo.convertTo(to, PersonWithAgeDomain.class);

        assertThat(res).isInstanceOf(PersonWithAgeDomainImpl.class);
        assertThat(res.getName()).isEqualTo("Joske");

        res = jTransfo.convertTo(to, PersonWithAgeDomainImpl.class);

        assertThat(res).isInstanceOf(PersonWithAgeDomainImpl.class);
        assertThat(res.getName()).isEqualTo("Joske");
    }

    @Test
    public void testReplaceClassToTo() {
        PersonWithAgeDomain domain = new PersonWithAgeDomainImpl();
        domain.setName("Joske");
        domain.setAge(18);

        PersonTo res = jTransfo.convertTo(domain, PersonTo.class);

        assertThat(res).isInstanceOf(PersonWithAgeTo.class);
        assertThat(res.getName()).isEqualTo("Joske");

        res = jTransfo.convertTo(domain, PersonWithAgeTo.class);

        assertThat(res).isInstanceOf(PersonWithAgeTo.class);
        assertThat(res.getName()).isEqualTo("Joske");
    }
    
    private class StoriedHouseClassReplacer implements ClassReplacer {

        @Override
        public Class replaceClass(Class clazz) {
            if (clazz == PersonWithAgeDomain.class) return PersonWithAgeDomainImpl.class;
            if (clazz == PersonTo.class) return PersonWithAgeTo.class;
            return clazz;
        }
    }

}

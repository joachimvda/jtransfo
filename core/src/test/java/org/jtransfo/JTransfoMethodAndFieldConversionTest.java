/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.MixedMethodAndFieldDomain;
import org.jtransfo.object.MixedMethodAndFieldTo;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JTransfoMethodAndFieldConversionTest {

    private JTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        jTransfo = JTransfoFactory.get();
    }

    @Test
    public void testToTo() throws Exception {
        // init fields with private getter in domain with 4 chars to verify getter is called
        MixedMethodAndFieldDomain domain = new MixedMethodAndFieldDomain("aaa", "bbb", "cccc", "ddd", "eeee");

        MixedMethodAndFieldTo res = jTransfo.convertTo(domain, MixedMethodAndFieldTo.class);

        assertThat(res.getFieldAlternate()).isEqualTo("aaa");
        assertThat(res.getFieldWithPublicAccessorsAlternate()).isEqualTo("bbb");
        assertThat(res.getFieldWithPrivateAccessorsAlternate()).isEqualTo("ccc");
        assertThat(res.getFieldWithPublicGetterAlternate()).isEqualTo("ddd");
        assertThat(res.getFieldWithPrivateGetterAlternate()).isEqualTo("eee");
    }

    @Test
    public void testToDomain() throws Exception {
        // init fields with private setter in domain with 4 chars to verify setter is called
        MixedMethodAndFieldTo to = new MixedMethodAndFieldTo("aaa", "bbbb", "cccc", "ddd", "eee");

        MixedMethodAndFieldDomain res = jTransfo.convertTo(to, MixedMethodAndFieldDomain.class);

        assertThat(res.getFieldAlternate()).isEqualTo("aaa");
        assertThat(res.getFieldWithPublicAccessors()).isEqualTo("bbb");
        assertThat(res.getFieldWithPrivateAccessorsAlternate()).isEqualTo("ccc");
        assertThat(res.getFieldWithPublicGetter()).isEqualTo("ddd");
        assertThat(res.getFieldWithPrivateGetterAlternate()).isEqualTo("eee");
    }
    
}

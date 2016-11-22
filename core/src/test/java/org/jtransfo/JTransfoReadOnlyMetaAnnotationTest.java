/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.ReadOnlyAnnotationTo;
import org.jtransfo.object.ReadOnlyGetterDomain;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JTransfoReadOnlyMetaAnnotationTest {

    private JTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        jTransfo = JTransfoFactory.get();
    }

    @Test
    public void testToTo() throws Exception {
        ReadOnlyGetterDomain domain = new ReadOnlyGetterDomain();
        domain.setId("bla");

        ReadOnlyAnnotationTo res = jTransfo.convert(domain, new ReadOnlyAnnotationTo());

        assertThat(res.getId()).isEqualTo("bla");
        assertThat(res.getTwice()).isEqualTo("blabla");
    }

    @Test
    public void testToDomain() throws Exception {
        ReadOnlyAnnotationTo to = new ReadOnlyAnnotationTo();
        to.setId("alb");
        to.setTwice("wrong");

        ReadOnlyGetterDomain res = jTransfo.convert(to, new ReadOnlyGetterDomain());

        assertThat(res.getId()).isEqualTo("alb");
        assertThat(res.getTwice()).isEqualTo("albalb");
    }
}

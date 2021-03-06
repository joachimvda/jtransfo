/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.hibernate;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for HibernateObjectReplacer.
 */
public class HibernateObjectReplacerTest {

    private HibernateObjectReplacer determinator = new HibernateObjectReplacer();

    @Test
    public void testNotAProxyCanConvert() throws Exception {
        Object object = new Object();
        assertThat(determinator.replaceObject(object)).isEqualTo(object);
    }

    // @todo should also test with a real Hibernate proxy, but don't know how

}

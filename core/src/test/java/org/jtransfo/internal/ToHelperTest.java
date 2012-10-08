/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.object.NoDomain;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

public class ToHelperTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ToHelper toHelper = new ToHelper();

    @Test
    public void isToTest() throws Exception {
        assertThat(toHelper.isTo(new SimpleClassNameTo())).isTrue();
        assertThat(toHelper.isTo(new SimpleClassTypeTo())).isTrue();
        assertThat(toHelper.isTo(new SimpleClassDomain())).isFalse();
    }

    @Test
    public void testGetDomainClass() throws Exception {
        assertThat(toHelper.getDomainClass(new SimpleClassNameTo())).isEqualTo(SimpleClassDomain.class);
        assertThat(toHelper.getDomainClass(new SimpleClassTypeTo())).isEqualTo(SimpleClassDomain.class);
    }

    @Test
    public void testGetNoDomainClass() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage(" not annotated with DomainClass.");
        assertThat(toHelper.getDomainClass(new NoDomain())).isNull();
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for {@link ToConverter}.
 */
public class ToConverterTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void getAddTest() throws Exception {
        ToConverter toConverter = new ToConverter();
        Converter dummy1 = new DummyConverter();
        Converter dummy2 = new DummyConverter();
        toConverter.addToDomain(dummy1);
        toConverter.addToTo(dummy2);

        assertThat(toConverter.getToDomain()).
                hasSize(1).
                contains(dummy1);
        assertThat(toConverter.getToTo()).
                hasSize(1).
                contains(dummy2);
    }

    @Test
    public void lockTest() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");

        ToConverter toConverter = new ToConverter();
        toConverter.addToDomain(new DummyConverter());
        toConverter.lock();

        toConverter.addToDomain(new DummyConverter());

    }

    private class DummyConverter implements Converter {
        @Override
        public void convert(Object source, Object target) {
            // nothing to do
        }
    }
}

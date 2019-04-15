/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonRodTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class JTransfoReadOnlyDomainTest {

    private static final String BLA = "something";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ConfigurableJTransfo jTransfo;

    @Before
    public void setup() {
        jTransfo = JTransfoFactory.get();

        AddressDomain address = new AddressDomain();
        address.setId(1L);
        ObjectFinder objectFinder = new ObjectFinder() {
            @Override
            public <T> T getObject(Class<T> domainClass, Object to, String... tags) throws JTransfoException {
                if (to instanceof AddressTo && 1L == ((AddressTo) to).getId()) {
                    return (T) address;
                }
                return null;
            }
        };
        jTransfo.with(objectFinder);
    }

    @Test
    public void testConvertToDomainRodFound() throws Exception {
        PersonRodTo to = new PersonRodTo();
        to.setName("Joske");
        to.setAddress(new AddressTo(1L));

        PersonDomain result = jTransfo.convert(to, new PersonDomain());

        assertThat(result.getName()).isEqualTo("Joske");
        assertThat(result.getAddress()).isNotNull();
        assertThat(result.getAddress().getId()).isEqualTo(1L);
    }

    @Test
    public void testConvertToDomainRodNotFound() throws Exception {
        PersonRodTo to = new PersonRodTo();
        to.setName("Joske");
        to.setAddress(new AddressTo(2L));

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of target class org.jtransfo.object.AddressDomain for source object org.jtransfo.object.AddressTo");

        jTransfo.convert(to, new PersonDomain());
    }

    @Test
    public void testConvertToDomainRodNull() throws Exception {
        PersonRodTo to = new PersonRodTo();
        to.setName("Joske");
        to.setAddress(null);

        PersonDomain result = jTransfo.convert(to, new PersonDomain());

        assertThat(result.getName()).isEqualTo("Joske");
        assertThat(result.getAddress()).isNull();
    }

}

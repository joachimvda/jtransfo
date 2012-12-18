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
import org.jtransfo.object.Gender;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonTo;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * More or less real-world test with object finders and type conversion.
 */
public class FinderAndConverterTest {

    private static final String NAME = "ikke";

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        JTransfoImpl impl = new JTransfoImpl();
        jTransfo = impl;

        AddressDomain[] addresses = new AddressDomain[10];
        for (int i = 0 ; i < 10 ; i++) {
            AddressDomain ad = new AddressDomain();
            ad.setId(Long.valueOf(i));
            ad.setAddress("Address " + i);
            addresses[i] = ad;
        }

        impl.getTypeConverters().add(new StringEnumTypeConverter(Gender.class));
        impl.updateTypeConverters();
        impl.getObjectFinders().add(new AddressFinder(addresses));
    }

    @Test
    public void  testWithFinderAndConverterToDomain() throws Exception {
        PersonTo to = new PersonTo();
        to.setGender("MALE");
        to.setLastChanged(new Date());
        to.setName(NAME);
        to.setAddress(new AddressTo(3L));

        PersonDomain domain = (PersonDomain) jTransfo.convert(to);
        assertThat(domain.getName()).isEqualTo(NAME);
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
        assertThat(domain.getLastChanged()).isNull();
    }

    @Test
    public void  testWithFinderAndConverterToTo() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName(NAME);
        Date now = new Date();
        domain.setLastChanged(now);
        AddressDomain address = new AddressDomain();
        address.setId(7L);
        address.setAddress("Kerkstraat");
        domain.setAddress(address);

        PersonTo to = jTransfo.convert(domain, new PersonTo());
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(7L);
        assertThat(to.getLastChanged()).isEqualTo(now);
    }

    private class AddressFinder implements ObjectFinder {

        private AddressDomain[] addresses;

        public AddressFinder(AddressDomain[] addresses) {
            this.addresses = addresses;
        }

        public <T> T getObject(Class<T> domainClass, Object to) {
            if (domainClass.isAssignableFrom(AddressDomain.class) && to instanceof AddressTo) {
                return (T) addresses[((AddressTo) to).getId().intValue()];
            }
            return null;
        }
    }
}

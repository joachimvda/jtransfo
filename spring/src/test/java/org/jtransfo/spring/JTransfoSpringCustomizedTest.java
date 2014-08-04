/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring;

import org.jtransfo.JTransfo;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.spring.domain.PersonDomain;
import org.jtransfo.spring.domain.PersonTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import javax.validation.ConstraintViolationException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:org/jtransfo/spring/jTransfoContext.xml", "testContext.xml"})
public class JTransfoSpringCustomizedTest {

    private static final String NAME = "ikke";

    @Autowired
    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void  testWithFinderAndConverterAndInterceptorsToDomain() throws Exception {
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
        assertThat(domain.getLastChanged()).isNotNull();
        assertThat(domain.getLastChanged().getTime()).isGreaterThan(System.currentTimeMillis() - 5000); // less than 5s
        assertThat(domain.getExtra()).isEqualTo("Extra sleep.");
    }

    @Test
    public void  testValidation() throws Exception {
        PersonTo to = new PersonTo();
        to.setGender("MALE");
        to.setLastChanged(new Date());
        to.setName(null); // fails validation !
        to.setAddress(new AddressTo(3L));

        exception.expect(ConstraintViolationException.class);
        exception.expectMessage("interpolatedMessage='may not be null'");

        jTransfo.convert(to);
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

}

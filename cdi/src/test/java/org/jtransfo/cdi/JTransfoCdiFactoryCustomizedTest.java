/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jtransfo.JTransfo;
import org.jtransfo.cdi.domain.PersonDomain;
import org.jtransfo.cdi.domain.PersonTo;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.object.Gender;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.Date;
import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Arquillian.class)
public class JTransfoCdiFactoryCustomizedTest {

    private static final String NAME = "ikke";

    @Inject
    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addPackage("org.jtransfo.cdi")
                .addPackage("org.jtransfo.cdi.domain")
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }

    @Test
    public void  testWithFinderAndConverterAndInterceptorsToDomain() throws Exception {
        PersonTo to = new PersonTo();
        to.setGender("MALE");
        to.setLastChanged(new Date());
        to.setName(NAME);
        to.setAddress(new AddressTo(3L));

        PersonDomain domain = (PersonDomain) jTransfo.convert(to);
        assertThat(domain.getName()).isEqualTo(NAME);
        assertThat(domain.getGender()).isEqualTo(Gender.MALE);
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
        assertThat(domain.getLastChanged()).isNotNull();
        assertThat(domain.getLastChanged().getTime()).isGreaterThan(System.currentTimeMillis() - 1000); // less than 1s
        assertThat(domain.getExtra()).isEqualTo("Extra sleep.");
    }

    @Test
    public void testWithFinderAndConverterToTo() throws Exception {
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

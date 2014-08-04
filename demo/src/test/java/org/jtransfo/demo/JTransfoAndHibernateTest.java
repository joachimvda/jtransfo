/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo;

import org.hibernate.SessionFactory;
import org.jtransfo.JTransfo;
import org.jtransfo.demo.domain.AddressTo;
import org.jtransfo.demo.domain.Country;
import org.jtransfo.demo.domain.Person;
import org.jtransfo.demo.domain.PersonTo;
import org.jtransfo.demo.domain.VoiceContact;
import org.jtransfo.demo.domain.VoiceContactTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"applicationContext.xml"})
public class JTransfoAndHibernateTest {

    private static final Long PERSON_ID = 1000L;
    private static final Long ADDRESS_ID = 2000L;

    @Autowired
    private JTransfo jTransfo;

    @Autowired
    private SessionFactory sessionFactory;

    @Test
    @Transactional
    public void convertDatabaseObjectToTransfer() throws Exception {
        Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, PERSON_ID); // find preloaded object
        assertThat(person).isNotNull();
        PersonTo to = jTransfo.convert(person, new PersonTo());

        assertThat(to).isNotNull();
        assertThat(to.getId()).isEqualTo(PERSON_ID);
        assertThat(to.getName()).isEqualTo("John Doe");
        AddressTo address = to.getAddress();
        assertThat(address).isNotNull();
        assertThat(address.getId()).isEqualTo(ADDRESS_ID);
        assertThat(address.getAddress()).isEqualTo("Churchstreet 11");
        assertThat(address.getPostalCode()).isEqualTo("1234");
        assertThat(address.getLocation()).isEqualTo("Mytown");
        assertThat(address.getCountry()).isEqualTo("BE");
        List<VoiceContactTo> voiceContacts = to.getVoiceContacts();
        assertThat(voiceContacts).isNotNull();
        assertThat(voiceContacts).hasSize(2).extracting("type").contains("work", "private, skype");
        for (Object obj : voiceContacts) {
            assertThat(obj).isInstanceOf(VoiceContactTo.class); // verify that list content was converted
        }
    }

    @Test
    @Transactional
    public void convertTransferObjectToDatabase() throws Exception {
        PersonTo personTo = new PersonTo();
        AddressTo addressTo = new AddressTo();
        personTo.setId(PERSON_ID); // overwrite existing db object
        personTo.setName("Peter Gabriel");
        personTo.setAddress(addressTo);
        personTo.setComment("Clear!");
        addressTo.setAddress("Salsbury Hill");
        addressTo.setPostalCode("zzz");
        addressTo.setLocation("Batheaston");
        addressTo.setCountry(Country.GB.name());
        List<VoiceContactTo> voiceContacts = new ArrayList<VoiceContactTo>();
        VoiceContactTo vc = new VoiceContactTo();
        vc.setType("home");
        vc.setVoice("+32 3 123 45 78");
        voiceContacts.add(vc);
        personTo.setVoiceContacts(voiceContacts);

        Person person = (Person) jTransfo.convert(personTo);

        assertThat(person.getName()).isEqualTo("Peter Gabriel");
        assertThat(person.getComment()).isEqualTo("db-only info"); // different from value in transfer object
        assertThat(person.getAddress()).isNotNull();
        assertThat(person.getAddress().getAddress()).isEqualTo("Salsbury Hill");
        assertThat(person.getAddress().getPostalCode()).isEqualTo("zzz");
        assertThat(person.getAddress().getLocation()).isEqualTo("Batheaston");
        assertThat(person.getAddress().getCountry()).isEqualTo(Country.GB);
        assertThat(person.getVoiceContacts()).hasSize(1).extracting("type").contains("home");
        for (Object obj : person.getVoiceContacts()) {
            assertThat(obj).isInstanceOf(VoiceContact.class); // verify that list content was converted
        }

        // verify that object can be saved
        sessionFactory.getCurrentSession().saveOrUpdate(person);
    }

}

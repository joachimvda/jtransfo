/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

import lombok.Data;
import org.hibernate.annotations.ForeignKey;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SequenceGenerator;
import java.util.List;

/**
 * Simple representation of a person.
 */
@Data
@Entity(name = "person")
@SequenceGenerator(name = "seq", sequenceName = "person_seq")
public class Person implements Comparable<Person> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    @Basic
    private String name;

    @ManyToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    @ForeignKey(name = "person_address")
    private Address address;

    @Basic
    private String comment;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = false)
    @JoinColumn(name = "person_id")
    @ForeignKey(name = "person_voiceContact")
    private List<VoiceContact> voiceContacts;

    @Override
    public int compareTo(Person other) {
        return name.compareTo(other.name);
    }
}

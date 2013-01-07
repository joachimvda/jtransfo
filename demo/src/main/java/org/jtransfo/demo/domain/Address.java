/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

/**
 * Simple representation of an address.
 */
@Data
@Entity(name = "address")
@SequenceGenerator(name = "seq", sequenceName = "address_seq")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private Long id;

    @Basic
    private String address;

    @Basic
    private String postalCode;

    @Basic
    private String location;

    @Basic
    @Type(type = "countryType")
    private Country country = Country.BE;

}

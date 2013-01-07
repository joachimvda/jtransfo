/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

import lombok.Data;
import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;

/**
 * Transfer object for a person.
 */
@Data
@DomainClass(domainClass = Person.class)
public class PersonTo implements IdentifiedTo {

    private Long id;
    private String name;
    private AddressTo address;

    @MappedBy(readOnly = true)
    private String comment;
}


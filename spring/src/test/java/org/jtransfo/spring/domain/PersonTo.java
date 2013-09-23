/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring.domain;

import lombok.Data;
import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;
import org.jtransfo.object.AddressTo;

import java.util.Date;

/**
 * Person transfer object.
 */
@Data
@DomainClass(domainClass = PersonDomain.class)
public class PersonTo {

    private String name;
    private String gender;
    private AddressTo address;

    @MappedBy(readOnly = true)
    private Date lastChanged;

}

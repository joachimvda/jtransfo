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
 * Transfer object for address.
 */
@Data
@DomainClass(domainClass = Address.class)
public class AddressTo implements IdentifiedTo {

    @MappedBy(readOnly = true) // writable database id can cause Hibernate exceptions
    private Long id;
    private String address;
    private String postalCode;
    private String location;
    private String country;

}

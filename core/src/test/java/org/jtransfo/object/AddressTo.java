/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Address transfer object.
 */
@DomainClass(domainClass = AddressDomain.class)
public class AddressTo {

    private Long id;

    public AddressTo() {
    }

    public AddressTo(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AddressTo withId(Long id) {
        setId(id);
        return this;
    }

}

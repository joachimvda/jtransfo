/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;

import java.io.Serializable;
import java.util.Date;

/**
 * Person transfer object.
 */
@DomainClass(domainClass = PersonDomain.class)
public class MalePersonTo implements Serializable {

    private static final long serialVersionUID = 1L;

    @MappedBy(field = "gender")
    private static final String GENDER = "MALE";

    private String name;
    private AddressTo address;

    @MappedBy(readOnly = true)
    private Date lastChanged;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AddressTo getAddress() {
        return address;
    }

    public void setAddress(AddressTo address) {
        this.address = address;
    }

    public Date getLastChanged() {
        return lastChanged;
    }

    public void setLastChanged(Date lastChanged) {
        this.lastChanged = lastChanged;
    }
}

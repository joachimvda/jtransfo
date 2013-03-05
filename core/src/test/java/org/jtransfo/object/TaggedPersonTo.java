/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MapOnlies;
import org.jtransfo.MapOnly;
import org.jtransfo.MappedBy;

import java.util.Date;

/**
 * Person transfer object.
 */
@DomainClass(domainClass = PersonDomain.class)
public class TaggedPersonTo {

    @MapOnlies({
        @MapOnly("create"),
        @MapOnly(value =  "update", readOnly = true)
    })
    private String name;

    @MapOnly("create")
    @MapOnlies({
            @MapOnly(value =  "*", readOnly = true)
    })
    @MappedBy(readOnly = true) // should not make a difference, overwritten by the ReadOnlies
    private String gender;

    @MapOnlies({
        @MapOnly({"create", "addressPolice"}),
        @MapOnly(value = "update", typeConverter = "always2")
    })
    private AddressTo address;

    @MappedBy(readOnly = true)
    private Date lastChanged;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

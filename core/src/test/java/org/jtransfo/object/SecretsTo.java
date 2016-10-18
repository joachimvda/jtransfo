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

/**
 * Secret person transfer object.
 */
@DomainClass(domainClass = PersonDomain.class)
public class SecretsTo {

    @MapOnlies({
        @MapOnly(value = "name", field = "name"),
        @MapOnly(value = MapOnly.ALWAYS, field = "gender")
    })
    private String genderOrName;

    @MapOnlies({
            @MapOnly(value =  "name", field = "name"),
            @MapOnly(value = "address", path = "address", field = "address")
    })
    private String addressOrName;

    public String getGenderOrName() {
        return genderOrName;
    }

    public void setGenderOrName(String genderOrName) {
        this.genderOrName = genderOrName;
    }

    public String getAddressOrName() {
        return addressOrName;
    }

    public void setAddressOrName(String addressOrName) {
        this.addressOrName = addressOrName;
    }

}

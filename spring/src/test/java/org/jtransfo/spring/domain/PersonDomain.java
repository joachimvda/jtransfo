/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring.domain;

import lombok.Data;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.Gender;

import java.util.Date;
import javax.validation.constraints.NotNull;

/**
 * Person domain object.
 */
@Data
public class PersonDomain {

    @NotNull
    private String name;
    private Gender gender;
    private AddressDomain address;
    private Date lastChanged;
    private String extra;

}

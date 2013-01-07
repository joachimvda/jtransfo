/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

/**
 * Default type definitions for Hibernate.
 */
@TypeDefs({
        @TypeDef(name = "countryType",
                defaultForType = Country.class,
                typeClass = EnumUserType.class,
                parameters = @Parameter(name = "enumClassName",
                        value = "org.jtransfo.demo.domain.Country"))
})
package org.jtransfo.demo.domain;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
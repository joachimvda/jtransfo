/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.PostConvert;
import org.jtransfo.PreConvert;

/**
 * Person transfer object with muptiple pre- and post converters.
 */
@PreConvert(converterClass = SkipMePreConverter.class)
@PreConvert("skipOther")
@PostConvert("deleteGender")
@PostConvert(converterClass = DoubleNamePostConverter.class)
@DomainClass(domainClass = PersonDomain.class)
public class PersonWithMultiplePrePostTo extends PersonTo {
}

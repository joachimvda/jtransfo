/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ToHelper;

/**
 * Type converter which only copies linked objects' fields to the transfer object. For convertering transfer object to
 * domain, the linked objects are looked up but the fields are not updated.
 */
public class ReadOnlyDomainTypeConverter extends AbstractToDomainTypeConverter {

    /**
     * Construct type converter using given ToHelper instance (sharing the reflection cache).
     *
     * @param jTransfo jTransfo engine for conversion
     * @param toHelper TO helper
     */
    public ReadOnlyDomainTypeConverter(JTransfo jTransfo, ToHelper toHelper) {
        super(jTransfo, toHelper);
    }

    @Override
    public Object doConvert(JTransfo jTransfo, Object toObject, Class<Object> domainType) throws JTransfoException {
        // only use object finder
        return jTransfo.findTarget(toObject, domainType);
    }
}

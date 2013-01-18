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
 * Recursively use jTransfo to convert fields which are themselves a transfer object.
 */
public class ToDomainTypeConverter extends AbstractToDomainTypeConverter {

    /**
     * Construct type converter using given ToHelper instance (sharing the reflection cache).
     *
     * @param jTransfo jTransfo engine for conversion
     * @param toHelper TO helper
     */
    public ToDomainTypeConverter(JTransfo jTransfo, ToHelper toHelper) {
        super(jTransfo, toHelper);
    }

    @Override
    public Object doConvert(JTransfo jTransfo, Object toObject, Class<Object> domainType) throws JTransfoException {
        return jTransfo.convert(toObject);
    }
}

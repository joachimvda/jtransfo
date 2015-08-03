/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;

/**
 * Recursively use jTransfo to convert fields which are themselves a transfer object.
 */
public class ToDomainTypeConverter extends AbstractToDomainTypeConverter {

    /**
     * Construct type converter for given JTransfo instance.
     *
     * @param jTransfo jTransfo engine for conversion
     */
    public ToDomainTypeConverter(JTransfo jTransfo) {
        setJTransfo(jTransfo);
    }

    @Override
    public Object doConvert(JTransfo jTransfo, Object toObject, SyntheticField domainField, String... tags)
            throws JTransfoException {
        if (null == toObject) {
            return null;
        }
        return jTransfo.convertTo(toObject, jTransfo.getDomainClass(toObject.getClass()), tags);
    }
}

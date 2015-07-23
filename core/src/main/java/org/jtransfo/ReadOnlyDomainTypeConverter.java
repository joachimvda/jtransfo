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
 * Type converter which only copies linked objects' fields to the transfer object. For converting transfer object to
 * domain, the linked objects are looked up but the fields are not updated.
 */
public class ReadOnlyDomainTypeConverter extends AbstractToDomainTypeConverter implements Named {


    private String name = "readOnlyDomain";

    @Override
    public Object doConvert(JTransfo jTransfo, Object toObject, SyntheticField domainField, String... tags)
            throws JTransfoException {
        Class<?> domainType = domainField.getType();
        // only use object finder
        if (null != toObject) {
            domainType = jTransfo.getDomainClass(jTransfo.getObjectClass(toObject));
        }
        return jTransfo.findTarget(toObject, domainType);
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Set the name for the type converter.
     *
     * @param name type converter name
     */
    public void setName(String name) {
        this.name = name;
    }
}

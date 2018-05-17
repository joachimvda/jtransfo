/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.reflect.Type;

/**
 * Type converter for converting lists with object of specific type. Can only be used as declared converter.
 * <p>
 * Similar to {@link ReadOnlyDomainTypeConverter} this does a full conversion from domain to transfer object but will
 * only find the objects and not update the fields when converting to the domain object. This way list membership is
 * updated, but the domain objects remain unmodified.
 * </p>
 */
public class ReadOnlyDomainAutomaticSetTypeConverter extends AutomaticSetTypeConverter {

    /**
     * No-arguments constructor, jTransfo instance needs to be injected explicitly.
     */
    public ReadOnlyDomainAutomaticSetTypeConverter() {
        // nothing to do
    }

    /**
     * Constructor which defined custom name.
     *
     * @param name name
     */
    public ReadOnlyDomainAutomaticSetTypeConverter(String name) {
        super(name);
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        return false; // readOnlyDomain needs to be declared explicitly
    }

    @Override
    public Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
            throws JTransfoException {
        return jTransfo.findTarget(toObject, domainObjectType, tags);
    }

}

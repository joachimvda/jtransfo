/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Type converter for converting a set with objects of specific type. Can only be used as declared converter.
 */
public class SetTypeConverter extends AbstractSetTypeConverter {

    /**
     * Construct type converter for converting a set, assign given name and use given transfer object type.
     *
     * @param name name for type converter, for use in {@link MappedBy#typeConverter()}
     * @param toType transfer object type
     */
    public SetTypeConverter(String name, Class<?> toType) {
        super(name, toType);
    }

    @Override
    public Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
            throws JTransfoException {
        return jTransfo.convertTo(toObject, domainObjectType, tags);
    }

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract type converter for converting lists with object of specific type. Can only be used as declared converter.
 */
public abstract class AbstractListTypeConverter implements TypeConverter<List, List>, Named, NeedsJTransfo {

    private String name;
    private Class<?> toType;
    private JTransfo jTransfo;

    /**
     * Construct type converter for converting a list, assign given name and use given transfer object type.
     *
     * @param name name for type converter, for use in {@link MappedBy#typeConverter()}
     * @param toType transfer object type
     */
    public AbstractListTypeConverter(String name, Class<?> toType) {
        this.name = name;
        this.toType = toType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setJTransfo(JTransfo jTransfo) {
        this.jTransfo = jTransfo;
    }

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        return false;  // never use automatically
    }

    /**
     * Do the actual conversion of one object.
     *
     * @param jTransfo jTransfo instance in use
     * @param toObject transfer object
     * @param domainObjectType domain object type
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public abstract Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType)
            throws JTransfoException;

    @Override
    public List convert(List toObjects, Class<List> domainFieldType) throws JTransfoException {
        if (null == toObjects) {
            return null;
        }
        List<Object> res = new ArrayList<Object>();
        for (Object to : toObjects) {
            res.add(doConvertOne(jTransfo, to, jTransfo.getDomainClass(to.getClass())));
        }
        return res;
    }

    @Override
    public List reverse(List domainObjects, Class<List> toFieldType) throws JTransfoException {
        if (null == domainObjects) {
            return null;
        }
        List<Object> res = new ArrayList<Object>();
        for (Object domain : domainObjects) {
            res.add(jTransfo.convertTo(domain, jTransfo.getToSubType(toType, domain)));
        }
        return res;
    }
}

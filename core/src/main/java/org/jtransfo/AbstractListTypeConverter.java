/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract type converter for converting lists with object of specific type. Can only be used as declared converter.
 */
public abstract class AbstractListTypeConverter implements TypeConverter<List, List>, Named, NeedsJTransfo {

    private String name;
    private Class<?> toType;
    private JTransfo jTransfo;
    private boolean keepNullList;
    private boolean alwaysNewList;
    private boolean sortList;

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
    public List convert(List toObjects, SyntheticField domainField, Object domainObject) throws JTransfoException {
        if (null == toObjects) {
            return getNullList();
        }
        List<Object> res = newList(domainField, domainObject);
        for (Object to : toObjects) {
            res.add(doConvertOne(jTransfo, to, jTransfo.getDomainClass(to.getClass())));
        }
        return sort(res);
    }

    @Override
    public List reverse(List domainObjects, SyntheticField toField, Object toObject) throws JTransfoException {
        if (null == domainObjects) {
            return getNullList();
        }
        List<Object> res = newList(toField, toObject);
        for (Object domain : domainObjects) {
            res.add(jTransfo.convertTo(domain, jTransfo.getToSubType(toType, domain)));
        }
        return sort(res);
    }

    private List<Object> newList(SyntheticField targetField, Object targetObject) {
        List<Object> res = null;
        if (!alwaysNewList && null != targetObject) {
            try {
                res = (List<Object>) targetField.get(targetObject);
            } catch (Exception exception) {
                res = null; // avoid problems in case of exception @todo should be logged
            }
        }
        if (null != res) {
            res.clear();
        } else {
            res = new ArrayList<Object>();
        }
        return sort(res);
    }

    private List<Object> sort(List<Object> list) {
        if (sortList) {
            if (null != list && list.size() > 1 && list.get(0) instanceof Comparable) {
                Collections.sort((List) list);
            }
        }
        return list;
    }

    private List getNullList() {
        return keepNullList ? null : new ArrayList<Object>();
    }

    /**
     * Set whether null values should be kept (true). When false (which is the default value), an empty list is set.
     *
     * @param keepNullList should null be kept as value or replaced by an empty list.
     */
    public void setKeepNullList(boolean keepNullList) {
        this.keepNullList = keepNullList;
    }

    /**
     * Set whether a new list should be used as container for the values. When false the list is reused if not null.
     *
     * @param alwaysNewList should null be kept as value or replaced by an empty list.
     */
    public void setAlwaysNewList(boolean alwaysNewList) {
        this.alwaysNewList = alwaysNewList;
    }

    /**
     * Should the list be sorted if the first member is Comparable?
     *
     * @param sortList true when list should be sorted
     */
    public void setSortList(boolean sortList) {
        this.sortList = sortList;
    }

}

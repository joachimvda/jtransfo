/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract type converter for converting lists with object of specific type. Can only be used as declared converter.
 */
public abstract class AbstractListTypeConverter implements TypeConverter<List, List>, Named, NeedsJTransfo {

    private final Logger log = LoggerFactory.getLogger(AbstractListTypeConverter.class);

    private String name;
    private Class<?> toType;
    private JTransfo jTransfo;
    private boolean keepNullList;
    private boolean alwaysNewList;
    private boolean sortList;
    private Supplier<List> emptyListSupplier = ArrayList::new;

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

    /**
     * Get the configured jTransfo instance.
     *
     * @return jTransfo instance
     */
    protected JTransfo getJTransfo() {
        return jTransfo;
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        return false;  // never use automatically
    }

    /**
     * Do the actual conversion of one object.
     *
     * @param jTransfo jTransfo instance in use
     * @param toObject transfer object
     * @param domainObjectType domain object type
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public abstract Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
            throws JTransfoException;

    @Override
    public List convert(List toObjects, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        if (null == toObjects) {
            return getNullList(domainField, domainObject);
        }
        List<Object> res = newList(domainField, domainObject);
        for (Object to : toObjects) {
            if (null == to) {
                res.add(null);
            } else {
                if (isPrimitiveOrString(to.getClass())) {
                    res.add(to);
                } else {
                    res.add(doConvertOne(jTransfo, to, jTransfo.getDomainClass(to.getClass()), tags));
                }
            }
        }
        return sort(res);
    }

    /**
     * Do the actual reverse conversion of one object.
     *
     * @param jTransfo jTransfo instance in use
     * @param domainObject domain object
     * @param toField field definition on the transfer object
     * @param toType configured to type for list
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public Object doReverseOne(
            JTransfo jTransfo, Object domainObject, SyntheticField toField, Class<?> toType, String... tags)
            throws JTransfoException {
        return jTransfo.convertTo(domainObject, jTransfo.getToSubType(toType, domainObject), tags);
    }


    @Override
    public List reverse(List domainObjects, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (null == domainObjects) {
            return getNullList(toField, toObject);
        }
        List<Object> res = newList(toField, toObject);
        for (Object domain : domainObjects) {
            if (isPrimitiveOrString(domain.getClass())) {
                res.add(domain);
            } else {
                res.add(doReverseOne(jTransfo, domain, toField, toType, tags));
            }
        }
        return sort(res);
    }

    private List<Object> newList(SyntheticField targetField, Object targetObject) {
        List<Object> res = null;
        if (!alwaysNewList && null != targetObject) {
            try {
                res = (List<Object>) targetField.get(targetObject);
            } catch (Exception exception) {
                log.error("Cannot get field " + targetField.getName() + " from " + targetObject, exception);
                res = null;
            }
        }
        if (null != res) {
            res.clear();
        } else {
            res = emptyListSupplier.get();
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

    private List getNullList(SyntheticField targetField, Object targetObject) {
        return keepNullList ? null : newList(targetField, targetObject);
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

    /**
     * Define a supplier for empty lists. By default {@link ArrayList} is used but this allows overriding that default.
     *
     * @param emptyListSupplier empty list supplier
     */
    public void setEmptyListSupplier(Supplier<List> emptyListSupplier) {
        this.emptyListSupplier = emptyListSupplier;
    }

    /**
     * Is the given class a primitive or String type?
     *
     * @param clazz class to test
     * @return true when primitive of String
     */
    protected boolean isPrimitiveOrString(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.isAssignableFrom(String.class);
    }

}

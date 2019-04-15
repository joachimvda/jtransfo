/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

/**
 * Type converter which only copies linked objects' fields to the transfer object. For converting transfer object to
 * domain, the linked objects are looked up but the fields are not updated.
 * This field automaticall detects if the field to convert is a list and uses
 * {@link ReadOnlyDomainAutomaticListTypeConverter} in that case.
 */
public class ReadOnlyDomainAutomaticTypeConverter
        extends AbstractToDomainTypeConverter
        implements Named, NeedsJTransfo {

    private String name = "readOnlyDomain";
    private ReadOnlyDomainAutomaticListTypeConverter rodListTypeConverter =
            new ReadOnlyDomainAutomaticListTypeConverter();
    private ReadOnlyDomainAutomaticSetTypeConverter rodSetTypeConverter =
            new ReadOnlyDomainAutomaticSetTypeConverter();

    @Override
    public void setJTransfo(JTransfo jTransfo) {
        super.setJTransfo(jTransfo);
        rodListTypeConverter.setJTransfo(jTransfo);
        rodSetTypeConverter.setJTransfo(jTransfo);
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        return false; // readOnlyDomain needs to be declared explicitly
    }

    @Override
    public Object convert(Object toObject, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        if (List.class.isAssignableFrom(domainField.getType())) {
            return rodListTypeConverter.convert((List<?>) toObject, domainField, domainObject, tags);
        }
        if (Set.class.isAssignableFrom(domainField.getType())) {
            return rodSetTypeConverter.convert((Set<?>) toObject, domainField, domainObject, tags);
        }
        return super.convert(toObject, domainField, domainObject, tags);
    }

    @Override
    public Object reverse(Object domainObject, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (List.class.isAssignableFrom(toField.getType())) {
            return rodListTypeConverter.reverse((List<?>) domainObject, toField, toObject, tags);
        }
        if (Set.class.isAssignableFrom(toField.getType())) {
            return rodSetTypeConverter.reverse((Set<?>) domainObject, toField, toObject, tags);
        }
        return super.reverse(domainObject, toField, toObject, tags);
    }

    @Override
    public Object doConvert(JTransfo jTransfo, Object toObject, SyntheticField domainField, String... tags)
            throws JTransfoException {
        Class<?> domainType = domainField.getType();
        // only use object finder
        if (null != toObject) {
            domainType = jTransfo.getDomainClass(toObject.getClass());
        }
        return jTransfo.findTarget(toObject, domainType, TagsUtil.add(tags, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN));
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

    /**
     * Set whether null values should be kept (true). When false (which is the default value), an empty list is set.
     *
     * @param keepNullList should null be kept as value or replaced by an empty list.
     */
    public void setKeepNullList(boolean keepNullList) {
        rodListTypeConverter.setKeepNullList(keepNullList);
    }

    /**
     * Set whether null values should be kept (true). When false (which is the default value), an empty set is set.
     *
     * @param keepNullSet should null be kept as value or replaced by an empty set.
     */
    public void setKeepNullSet(boolean keepNullSet) {
        rodSetTypeConverter.setKeepNullSet(keepNullSet);
    }

    /**
     * Set whether a new list should be used as container for the values. When false the list is reused if not null.
     *
     * @param alwaysNewList should null be kept as value or replaced by an empty list.
     */
    public void setAlwaysNewList(boolean alwaysNewList) {
        rodListTypeConverter.setAlwaysNewList(alwaysNewList);
    }

    /**
     * Set whether a new set should be used as container for the values. When false the set is reused if not null.
     *
     * @param alwaysNewSet should null be kept as value or replaced by an empty set.
     */
    public void setAlwaysNewSet(boolean alwaysNewSet) {
        rodSetTypeConverter.setAlwaysNewSet(alwaysNewSet);
    }

    /**
     * Should the list be sorted if the first member is Comparable?
     *
     * @param sortList true when list should be sorted
     */
    public void setSortList(boolean sortList) {
        rodListTypeConverter.setSortList(sortList);
    }

}

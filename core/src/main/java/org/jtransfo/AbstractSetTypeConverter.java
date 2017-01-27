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
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Abstract type converter for {@link Set} objects. You need to define the to object contents for the set.
 */
public abstract class AbstractSetTypeConverter implements TypeConverter<Set, Set>, Named, NeedsJTransfo {

    private final Logger log = LoggerFactory.getLogger(AbstractSetTypeConverter.class);

    private String name;
    private Class<?> toType;
    private JTransfo jTransfo;
    private boolean keepNullSet;
    private boolean alwaysNewSet;
    private Supplier<Set> emptySetSupplier = HashSet::new;

    /**
     * Construct type converter for converting a set, assign given name and use given transfer object type.
     *
     * @param name name for type converter, for use in {@link org.jtransfo.MappedBy#typeConverter()}
     * @param toType transfer object type
     */
    public AbstractSetTypeConverter(String name, Class<?> toType) {
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
     * @param tags tags which indicate which fields can be converted based on {@link org.jtransfo.MapOnly} annotations.
     * @return domain object
     * @throws JTransfoException oops, cannot convert
     */
    public abstract Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
            throws JTransfoException;

    @Override
    public Set convert(Set toObjects, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException {
        if (null == toObjects) {
            return getNullSet(domainField, domainObject);
        }
        Set<Object> res = newSet(domainField, domainObject);
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
        return res;
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
    public Set reverse(Set domainObjects, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException {
        if (null == domainObjects) {
            return getNullSet(toField, toObject);
        }
        Set<Object> res = newSet(toField, toObject);
        for (Object domain : domainObjects) {
            if (isPrimitiveOrString(domain.getClass())) {
                res.add(domain);
            } else {
                res.add(doReverseOne(jTransfo, domain, toField, toType, tags));
            }
        }
        return res;
    }

    private Set<Object> newSet(SyntheticField targetField, Object targetObject) {
        Set<Object> res = null;
        if (!alwaysNewSet && null != targetObject) {
            try {
                res = (Set<Object>) targetField.get(targetObject);
            } catch (Exception exception) {
                log.error("Cannot get field " + targetField.getName() + " from " + targetObject, exception);
                res = null;
            }
        }
        if (null != res) {
            res.clear();
        } else {
            res = emptySetSupplier.get();
        }
        return res;
    }

    private Set getNullSet(SyntheticField targetField, Object targetObject) {
        return keepNullSet ? null : newSet(targetField, targetObject);
    }

    /**
     * Set whether null values should be kept (true). When false (which is the default value), an empty set is set.
     *
     * @param keepNullSet should null be kept as value or replaced by an empty set.
     */
    public void setKeepNullSet(boolean keepNullSet) {
        this.keepNullSet = keepNullSet;
    }

    /**
     * Set whether a new set should be used as container for the values. When false the set is reused if not null.
     *
     * @param alwaysNewSet should null be kept as value or replaced by an empty list.
     */
    public void setAlwaysNewSet(boolean alwaysNewSet) {
        this.alwaysNewSet = alwaysNewSet;
    }

    /**
     * Define a supplier for empty sets. By default {@link HashSet} is used but this allows overriding that default.
     *
     * @param emptySetSupplier empty set supplier
     */
    public void setEmptySetSupplier(Supplier<Set> emptySetSupplier) {
        this.emptySetSupplier = emptySetSupplier;
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

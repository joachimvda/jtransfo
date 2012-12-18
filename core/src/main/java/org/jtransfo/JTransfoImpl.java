/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.internal.NewInstanceObjectFinder;
import org.jtransfo.internal.ToHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jTransfo main access point standard implementation.
 */
public class JTransfoImpl implements JTransfo {

    private ToHelper toHelper = new ToHelper();
    private ConverterHelper converterHelper = new ConverterHelper();
    private Map<Class, ToConverter> converters = new ConcurrentHashMap<Class, ToConverter>();
    private List<ObjectFinder> objectFinders = new ArrayList<ObjectFinder>();
    private List<TypeConverter> typeConverters = new ArrayList<TypeConverter>();

    /**
     * Constructor.
     */
    public JTransfoImpl() {
        objectFinders.add(new NewInstanceObjectFinder());

        typeConverters.add(new NoConversionTypeConverter());
        converterHelper.setTypeConvertersInOrder(typeConverters);
    }

    /**
     * Get the set of type converters which are used by this JTransfo instance.
     * <p/>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * <p/>
     * Changes in the list are not used until you call {@link #updateTypeConverters(java.util.List)}.
     *
     * @return current list of type converters.
     */
    public List<TypeConverter> getTypeConverters() {
        return typeConverters;
    }

    /**
     * Update the list of type converters which is used.
     * <p/>
     * When null is passed, this updates the changes to the internal list (see {@link #getTypeConverters()}.
     * Alternatively, you can pass the new list explicitly.
     *
     * @param newConverters new list of type converters
     */
    public void updateTypeConverters(List<TypeConverter> newConverters) {
        if (null != newConverters) {
            typeConverters.clear();
            typeConverters.addAll(newConverters);

        }
        converterHelper.setTypeConvertersInOrder(typeConverters);
    }

    /**
     * Get the list of {@link ObjectFinder}s to allow customization.
     * <p/>
     * The elements are tried in reverse order (from end to start of list).
     *
     * @return list of object finders
     */
    public List<ObjectFinder> getObjectFinders() {
        return objectFinders;
    }

    @Override
    public <T> T convert(Object source, T target) {
        if (null == source || null == target) {
            throw new JTransfoException("Source and target are required to be not-null.");
        }
        boolean targetIsTo = false;
        if (!toHelper.isTo(source)) {
            targetIsTo = true;
            if (!toHelper.isTo(target)) {
                throw new JTransfoException("Neither source nor target are annotated with DomainClass on classes " +
                        source.getClass().getName() + " and " + target.getClass().getName() + ".");
            }
        }

        List<Converter> converters = targetIsTo ? getToToConverters(target.getClass(), source.getClass()) :
                getToDomainConverters(source.getClass(), target.getClass());
        for (Converter converter : converters) {
            converter.convert(source, target);
        }

        return target;
    }

    @Override
    public Object convert(Object source) {
        Class<?> domainClass = toHelper.getDomainClass(source);
        int i = objectFinders.size() - 1;
        Object target = null;
        while (null == target && i >= 0) {
            target = objectFinders.get(i--).getObject(domainClass, source);
        }
        if (null == target) {
            throw new JTransfoException("Cannot create instance of domain class " + domainClass.getName() +
                    " for transfer object " + source + ".");
        }
        return convert(source, target);
    }

    private List<Converter> getToToConverters(Class toClass, Class domainClass) {
        return getToConverter(toClass, domainClass).getToTo();
    }

    private List<Converter> getToDomainConverters(Class toClass, Class domainClass) {
        return getToConverter(toClass, domainClass).getToDomain();
    }

    private ToConverter getToConverter(Class toClass, Class domainClass) {
        Class clazz = toClass.getClass();
        ToConverter toConverter = converters.get(clazz);
        if (null == toConverter) {
            toConverter = converterHelper.getToConverter(toClass, domainClass);
            converters.put(clazz, toConverter);
        }
        return toConverter;
    }
}

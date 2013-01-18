/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.internal.LockableList;
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
    private List<ObjectFinder> modifyableObjectFinders = new ArrayList<ObjectFinder>();
    private LockableList<ObjectFinder> objectFinders = new LockableList<ObjectFinder>();
    private List<TypeConverter> modifyableTypeConverters = new ArrayList<TypeConverter>();

    /**
     * Constructor.
     */
    public JTransfoImpl() {
        modifyableObjectFinders.add(new NewInstanceObjectFinder());
        updateObjectFinders();

        modifyableTypeConverters.add(new NoConversionTypeConverter());
        modifyableTypeConverters.add(new ToDomainTypeConverter(this, toHelper));
        updateTypeConverters();
    }

    /**
     * Get the set of type converters which are used by this jTransfo instance.
     * <p/>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * <p/>
     * Changes in the list are not used until you call {@link #updateTypeConverters()}.
     *
     * @return current list of type converters.
     */
    public List<TypeConverter> getTypeConverters() {
        return modifyableTypeConverters;
    }

    /**
     * Update the list of type converters which is used based on the internal list (see {@link #getTypeConverters()}.
     */
    public void updateTypeConverters() {
        updateTypeConverters(null);
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
            modifyableTypeConverters.clear();
            modifyableTypeConverters.addAll(newConverters);

        }
        for (TypeConverter tc : modifyableTypeConverters) {
            if (tc instanceof NeedsJTransfo) {
                ((NeedsJTransfo) tc).setJTransfo(this);
            }
        }
        converterHelper.setTypeConvertersInOrder(modifyableTypeConverters);
    }

    /**
     * Get the list of {@link ObjectFinder}s to allow customization.
     * <p/>
     * The elements are tried in reverse order (from end to start of list).
     * <p/>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * <p/>
     * Changes in the list are not used until you call {@link #updateObjectFinders()}.
     *
     * @return list of object finders
     */
    public List<ObjectFinder> getObjectFinders() {
        return modifyableObjectFinders;
    }

    /**
     * Update the list of object finders which is used based on the internal list (see {@link #getObjectFinders()}.
     */
    public void updateObjectFinders() {
        updateObjectFinders(null);
    }

    /**
     * Update the list of object finders which is used.
     * <p/>
     * When null is passed, this updates the changes to the internal list (see {@link #getObjectFinders()}.
     * Alternatively, you can pass the new list explicitly.
     *
     * @param newObjectFinders new list of type converters
     */
    public void updateObjectFinders(List<ObjectFinder> newObjectFinders) {
        if (null != newObjectFinders) {
            modifyableObjectFinders.clear();
            modifyableObjectFinders.addAll(newObjectFinders);

        }
        LockableList<ObjectFinder> newList = new LockableList<ObjectFinder>();
        newList.addAll(modifyableObjectFinders);
        newList.lock();
        objectFinders = newList;
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

        List<Converter> converters = targetIsTo ? getToToConverters(target.getClass()) :
                getToDomainConverters(source.getClass());
        for (Converter converter : converters) {
            converter.convert(source, target);
        }

        return target;
    }

    @Override
    public Object convert(Object source) {
        if (null == source) {
            return null;
        }
        Class<?> domainClass = getDomainClass(source.getClass());
        return convertTo(source,  domainClass);
    }

    @Override
    public <T> T convertTo(Object source, Class<T> targetClass) {
        if (null == source) {
            return null;
        }
        return (T) convert(source, findTarget(source, targetClass));
    }

    @Override
    public <T> T findTarget(Object source, Class<T> targetClass) {
        if (null == source) {
            return null;
        }
        int i = objectFinders.size() - 1;
        T target = null;
        while (null == target && i >= 0) {
            target = objectFinders.get(i--).getObject(targetClass, source);
        }
        if (null == target) {
            throw new JTransfoException("Cannot create instance of target class " + targetClass.getName() +
                    " for source object " + source + ".");
        }
        return target;
    }

    @Override
    public Class<?> getDomainClass(Class<?> toClass) {
        return toHelper.getDomainClass(toClass);
    }

    private List<Converter> getToToConverters(Class toClass) {
        return getToConverter(toClass).getToTo();
    }

    private List<Converter> getToDomainConverters(Class toClass) {
        return getToConverter(toClass).getToDomain();
    }

    private ToConverter getToConverter(Class toClass) {
        ToConverter toConverter = converters.get(toClass);
        if (null == toConverter) {
            Class<?> domainClass = getDomainClass(toClass);
            toConverter = converterHelper.getToConverter(toClass, domainClass);
            converters.put(toClass, toConverter);
        }
        return toConverter;
    }
}

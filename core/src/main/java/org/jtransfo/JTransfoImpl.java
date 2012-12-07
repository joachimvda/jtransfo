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
import org.jtransfo.internal.ReflectionHelper;
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
    private ReflectionHelper reflectionHelper = new ReflectionHelper();
    private ConverterHelper converterHelper = new ConverterHelper();
    private Map<Class, ToConverter> converters = new ConcurrentHashMap<Class, ToConverter>();
    private List<ObjectFinder> objectFinders = new ArrayList<ObjectFinder>();

    /**
     * Constructor.
     */
    public JTransfoImpl() {
        objectFinders.add(new NewInstanceObjectFinder());
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
                    " for transfer object "+ source + ".");
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

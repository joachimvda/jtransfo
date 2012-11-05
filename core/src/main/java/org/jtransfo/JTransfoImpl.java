/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.ToHelper;

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
        Class domainClass = toHelper.getDomainClass(source);
        try {
            // @todo allow creation to be pluggable, could need domain lookup based on a field in the source
            Object target = reflectionHelper.newInstance(domainClass);
            return convert(source, target);
        } catch (InstantiationException ie) {
            throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName() + ".", ie);
        } catch (IllegalAccessException ie) {
            throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName() + ".", ie);
        }
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

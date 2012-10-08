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
import org.jtransfo.internal.ToHelper;

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

    /**
     * Fill the target object with the values from the source object.
     * <p/>
     * This will write all values from the transfer object, other fields are not touched.
     *
     * @param source source object
     * @param target target object
     */
    public <T> T convert(Object source, T target) {
        if (null == source || null == target) {
            throw new JTransfoException("Source and target are required to be not-null.");
        }
        boolean targetIsTo = false;
        if (!toHelper.isTo(source)) {
            targetIsTo = true;
            if (!toHelper.isTo(target)) {
                throw new JTransfoException("Neither source nor target are annotated with DomainClass on classes " +
                        source.getClass().getName() + " and " + target.getClass().getName());
            }
        }

        List<Converter> converters = targetIsTo ? getToToConverters(target, source) :
                getToDomainConverters(source, target);
        for (Converter converter : converters) {
            converter.convert(source, target);
        }

        return target;
    }

    /**
     * Create a new domain object from the source transfer object.
     * <p/>
     * This only works if the domain object has a no-arguments constructor.
     *
     * @param source source transfer object
     * @return domain object
     */
    public Object convert(Object source) {
        Class domainClass = toHelper.getDomainClass(source);
        try {
            // @todo allow creation to be pluggable, could need domain lookup based on a field in the source
            Object target = domainClass.newInstance();
            return convert(source, target);
        } catch (InstantiationException ie) {
            throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName(), ie);
        } catch (IllegalAccessException ie) {
            throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName(), ie);
        }
    }

    private List<Converter> getToToConverters(Object to, Object domain) {
        return getToConverter(to, domain).getToTo();
    }

    private List<Converter> getToDomainConverters(Object to, Object domain) {
        return getToConverter(to, domain).getToDomain();
    }

    private ToConverter getToConverter(Object to, Object domain) {
        Class clazz = to.getClass();
        ToConverter toConverter = converters.get(clazz);
        if (null == toConverter) {
            toConverter = converterHelper.getToConverter(to, domain);
            converters.put(clazz, toConverter);
        }
        return toConverter;
    }
}

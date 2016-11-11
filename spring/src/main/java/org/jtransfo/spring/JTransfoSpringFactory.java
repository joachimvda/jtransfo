/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring;

import org.jtransfo.ConfigurableJTransfo;
import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoFactory;
import org.jtransfo.ObjectFinder;
import org.jtransfo.ObjectReplacer;
import org.jtransfo.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Get JTransfo instance which is configured with objects found in the Spring application context.
 */
public class JTransfoSpringFactory {

    @Autowired(required = false)
    private List<ObjectFinder> objectFinders;

    @Autowired(required = false)
    private List<TypeConverter> typeConverters;

    @Autowired(required = false)
    private List<ConvertInterceptor> convertInterceptors;

    @Autowired(required = false)
    private List<ObjectReplacer> objectReplacers;

    /**
     * Get {@link JTransfo} instance with object finders, object replacers, convert interceptors and
     * type converters from Spring configuration.
     *
     * @return {@link JTransfo} instance
     */
    public JTransfo get() {
        ConfigurableJTransfo jTransfo = JTransfoFactory.get();
        if (null != typeConverters) {
            jTransfo.getTypeConverters().addAll(typeConverters);
            jTransfo.updateTypeConverters();
        }

        if (null != objectFinders) {
            jTransfo.getObjectFinders().addAll(objectFinders);
            jTransfo.updateObjectFinders();
        }

        if (null != convertInterceptors) {
            Collections.sort(convertInterceptors, new AnnotationAwareOrderComparator());
            jTransfo.getConvertInterceptors().addAll(convertInterceptors);
            jTransfo.updateConvertInterceptors();
        }

        if (null != objectReplacers) {
            List<ObjectReplacer> orderedInterceptors = new ArrayList<>();
            for (ObjectReplacer objectClassDeterminator : objectReplacers) {
                orderedInterceptors.add(objectClassDeterminator);
            }
            Collections.sort(orderedInterceptors, new AnnotationAwareOrderComparator());
            jTransfo.getObjectReplacers().addAll(orderedInterceptors);
            jTransfo.updateObjectReplacers();
        }
        return jTransfo;
    }

}

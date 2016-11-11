/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi;

import org.jtransfo.ConfigurableJTransfo;
import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoFactory;
import org.jtransfo.ObjectFinder;
import org.jtransfo.ObjectReplacer;
import org.jtransfo.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * CDI implementation of {@link org.jtransfo.JTransfo}.
 */
@ApplicationScoped
public class JTransfoCdiFactory {

    @Inject
    private Instance<ObjectFinder> objectFinders;

    @Inject
    // the <?, ?> is needed to make this work on all CDI containers
    // specifically WildFly 8.0.0.Final (and possibly the Weld version included) require this or only
    // TypeConverter<Object, Object> instances are matched.
    private Instance<TypeConverter<?, ?>> typeConverters;

    @Inject
    private Instance<ConvertInterceptor> convertInterceptors;

    @Inject
    private Instance<ObjectReplacer> objectReplacers;

    /**
     * Get {@link JTransfo} instance with object finders, object replacers, convert interceptors and
     * type converters from CDI configuration.
     *
     * @return {@link JTransfo} instance
     */
    @Produces
    public JTransfo get() {
        ConfigurableJTransfo jTransfo = JTransfoFactory.get();
        if (null != typeConverters) {
            for (TypeConverter typeConverter : typeConverters) {
                jTransfo.getTypeConverters().add(typeConverter);
            }
            jTransfo.updateTypeConverters();
        }

        if (null != objectFinders) {
            for (ObjectFinder objectFinder : objectFinders) {
                jTransfo.getObjectFinders().add(objectFinder);
            }
            jTransfo.updateObjectFinders();
        }

        if (null != convertInterceptors) {
            List<ConvertInterceptor> orderedInterceptors = new ArrayList<>();
            for (ConvertInterceptor convertInterceptor : convertInterceptors) {
                orderedInterceptors.add(convertInterceptor);
            }
            Collections.sort(orderedInterceptors, new AnnotationAwareOrderComparator());
            jTransfo.getConvertInterceptors().addAll(orderedInterceptors);
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

    /**
     * Order interceptors based on the {@link org.jtransfo.cdi.InterceptorOrder} annotation if present.
     */
    private class AnnotationAwareOrderComparator implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            // Direct evaluation instead of Integer.compareTo to avoid unnecessary object creation.
            int i1 = getOrder(o1);
            int i2 = getOrder(o2);
            return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
        }

        private int getOrder(Object obj) {
            if (obj != null) {
                InterceptorOrder order = obj.getClass().getAnnotation(InterceptorOrder.class);
                if (order != null) {
                    return order.value();
                }
            }
            return InterceptorOrder.DEFAULT_ORDER;
        }

    }

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfoImpl;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 * CDI implementation of {@link org.jtransfo.JTransfo}.
 */
@ApplicationScoped
public class JTransfoCdi extends JTransfoImpl {

    @Inject
    private Instance<ObjectFinder> objectFinders;

    @Inject
    // the <?, ?> is needed to make this work on all CDI containers
    // specifically WildFly 8.0.0.Final (and possibly the Weld version included) require this or only
    // TypecConverter<Object, Object> instances are matched.
    private Instance<TypeConverter<?, ?>> typeConverters;

    @Inject
    private Instance<ConvertInterceptor> convertInterceptors;

    /**
     * Get object finders and type converters from Spring configuration.
     */
    @PostConstruct
    protected void postConstruct() {
        if (null != typeConverters) {
            for (TypeConverter typeConverter : typeConverters) {
                getTypeConverters().add(typeConverter);
            }
            updateTypeConverters();
        }

        if (null != objectFinders) {
            for (ObjectFinder objectFinder : objectFinders) {
                getObjectFinders().add(objectFinder);
            }
            updateObjectFinders();
        }

        if (null != convertInterceptors) {
            List<ConvertInterceptor> orderedInterceptors = new ArrayList<ConvertInterceptor>();
            for (ConvertInterceptor convertInterceptor : convertInterceptors) {
                orderedInterceptors.add(convertInterceptor);
            }
            Collections.sort(orderedInterceptors, new AnnotationAwareOrderComparator());
            getConvertInterceptors().addAll(orderedInterceptors);
            updateConvertInterceptors();
        }
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

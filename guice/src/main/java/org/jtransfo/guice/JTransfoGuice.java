/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.guice;

import java.util.List;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfoImpl;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;

import com.google.inject.Inject;

/**
 * Guice implementation of {@link org.jtransfo.JTransfo}.
 * 
 * See the {@link ExampleModule} on how you could configure this.
 */
public class JTransfoGuice extends JTransfoImpl {

    /**
     * 
     * Constructor for the Guice dependency injection.
     * 
     * @param objectFinders a {@link List} of {@link ObjectFinder} that needs to be added to the JTransfo
     * @param typeConverters a {@link List} of {@link TypeConverter} that needs to be added to the JTransfo
     * @param convertInterceptors a {@link List} of {@link ConvertInterceptor} that needs 
     *              to be added to the JTransfo
     */
    @Inject
    public JTransfoGuice(List<ObjectFinder> objectFinders,
            @SuppressWarnings("rawtypes") List<TypeConverter> typeConverters,
            List<ConvertInterceptor> convertInterceptors) {
        super();
        if (null != typeConverters) {
            getTypeConverters().addAll(typeConverters);
            updateTypeConverters();
        }

        if (null != objectFinders) {
            getObjectFinders().addAll(objectFinders);
            updateObjectFinders();
        }

        if (null != convertInterceptors) {
            getConvertInterceptors().addAll(convertInterceptors);
            updateConvertInterceptors();
        }

    }
}
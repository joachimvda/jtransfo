/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring;

import org.jtransfo.JTransfoImpl;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import javax.annotation.PostConstruct;

/**
 * Spring implementation of {@link org.jtransfo.JTransfo}.
 */
public class JTransfoSpring extends JTransfoImpl {

    @Autowired(required = false)
    private List<ObjectFinder> objectFinders;

    @Autowired(required = false)
    private List<TypeConverter> typeConverters;

    /**
     * Get object finders and type converters from Spring configuration.
     */
    @PostConstruct
    protected void postConstruct() {
        if (null != typeConverters) {
            getTypeConverters().addAll(typeConverters);
            updateTypeConverters();
        }

        if (null != objectFinders) {
            getObjectFinders().addAll(objectFinders);
            updateObjectFinders();
        }

    }
}

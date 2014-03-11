/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */
package org.jtransfo.guice.examples;

import org.jtransfo.JTransfoException;
import org.jtransfo.ObjectFinder;

import com.google.inject.Inject;

public class SampleObjectFinder implements ObjectFinder {

    private final ISampleService sampleService;
    
    @Inject
    public SampleObjectFinder(final ISampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Override
    public <T> T getObject(Class<T> domainClass, Object to)
            throws JTransfoException {
        // TODO Auto-generated method stub
        return null;
    }

    public ISampleService getSampleService() {
        return sampleService;
    }

}

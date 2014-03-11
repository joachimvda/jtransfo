/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */
package org.jtransfo.guice.examples;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;

import com.google.inject.Inject;

public class SampleConvertInterceptor implements ConvertInterceptor {

    private final ISampleService sampleService;
    
    @Inject
    public SampleConvertInterceptor(final ISampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo,
            ConvertSourceTarget next, String... tags) {
        // TODO Auto-generated method stub
        return null;
    }

    public ISampleService getSampleService() {
        return sampleService;
    }

}

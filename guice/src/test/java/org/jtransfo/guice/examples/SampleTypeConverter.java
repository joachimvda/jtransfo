/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */
package org.jtransfo.guice.examples;

import org.jtransfo.JTransfoException;
import org.jtransfo.TypeConverter;
import org.jtransfo.internal.SyntheticField;

import com.google.inject.Inject;

public class SampleTypeConverter implements TypeConverter<String, String> {
    
    private final ISampleService sampleService;
    
    @Inject
    public SampleTypeConverter(final ISampleService sampleService) {
        this.sampleService = sampleService;
    }

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String convert(String object, SyntheticField domainField,
            Object domainObject, String... tags) throws JTransfoException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String reverse(String object, SyntheticField toField,
            Object toObject, String... tags) throws JTransfoException {
        // TODO Auto-generated method stub
        return null;
    }

    public ISampleService getSampleService() {
        return sampleService;
    }

}

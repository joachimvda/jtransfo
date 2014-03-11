/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.guice;

import java.util.LinkedList;
import java.util.List;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfo;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;
import org.jtransfo.guice.examples.ISampleService;
import org.jtransfo.guice.examples.SampleConvertInterceptor;
import org.jtransfo.guice.examples.SampleObjectFinder;
import org.jtransfo.guice.examples.SampleService;
import org.jtransfo.guice.examples.SampleTypeConverter;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ExampleModule extends AbstractModule {

    @Override
    protected void configure() {
        // TODO Auto-generated method stub
        bind(JTransfo.class).to(JTransfoGuice.class);
        bind(ISampleService.class).to(SampleService.class);
    }

    @Provides
    List<TypeConverter> jTransfoTypeConverters(SampleTypeConverter typeConverter) {
        List<TypeConverter> result = new LinkedList<TypeConverter>();
        result.add(typeConverter);
        return result;
    }

    @Provides
    List<ObjectFinder> jTransfoObjectFinders(SampleObjectFinder objectFinder) {
        LinkedList<ObjectFinder> result = new LinkedList<ObjectFinder>();
        result.add(objectFinder);
        return result;
    }

    @Provides
    List<ConvertInterceptor> jTransfoConverterInterceptors(SampleConvertInterceptor convertInterceptor) {
        LinkedList<ConvertInterceptor> result = new LinkedList<ConvertInterceptor>();
        result.add(convertInterceptor);
        return result;
    }

}

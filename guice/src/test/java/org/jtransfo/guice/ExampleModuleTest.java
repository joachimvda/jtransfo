/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */
package org.jtransfo.guice;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoImpl;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;
import org.jtransfo.guice.examples.SampleConvertInterceptor;
import org.jtransfo.guice.examples.SampleObjectFinder;
import org.jtransfo.guice.examples.SampleService;
import org.jtransfo.guice.examples.SampleTypeConverter;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ExampleModuleTest {

    @Test
    public void test() {
        Injector injector = Guice.createInjector(new ExampleModule());
        
        JTransfo instance = injector.getInstance(JTransfo.class);
        assertNotNull(instance);
        assertTrue(instance instanceof JTransfoImpl);
        JTransfoImpl transfoImpl = (JTransfoImpl) instance;

        List<TypeConverter> typeConverters = transfoImpl.getTypeConverters();
        assertNotNull(typeConverters);
        boolean sampleConverterFound = false;
        for (TypeConverter typeConverter : typeConverters) {
            if (typeConverter instanceof SampleTypeConverter) {
                sampleConverterFound = true;
                SampleTypeConverter sampleConverter = (SampleTypeConverter) typeConverter;
                assertNotNull(sampleConverter.getSampleService());
                assertTrue(sampleConverter.getSampleService() instanceof SampleService);
            }
        }
        assertTrue("The sampleConverter should be in the list.", sampleConverterFound);
        
        List<ObjectFinder> objectFinders = transfoImpl.getObjectFinders();
        assertNotNull(objectFinders);
        boolean sampleObjectFinderFound = false;
        for (ObjectFinder objectFinder : objectFinders) {
            if (objectFinder instanceof SampleObjectFinder) {
                sampleObjectFinderFound = true;
                SampleObjectFinder sampleObjectFinder= (SampleObjectFinder) objectFinder;
                assertNotNull(sampleObjectFinder.getSampleService());
                assertTrue(sampleObjectFinder.getSampleService() instanceof SampleService);
            }
        }
        assertTrue("The sample object finder should be in the list.", sampleObjectFinderFound);
        
        List<ConvertInterceptor> convertInterceptors = transfoImpl.getConvertInterceptors();
        assertNotNull(convertInterceptors);
        boolean sampleConvertInterceptorFound = false;
        for (ConvertInterceptor convertInterceptor : convertInterceptors) {
            if (convertInterceptor instanceof SampleConvertInterceptor) {
                sampleConvertInterceptorFound = true;
                SampleConvertInterceptor sampleConvertInterceptor = (SampleConvertInterceptor) convertInterceptor;
                assertNotNull(sampleConvertInterceptor.getSampleService());
                assertTrue(sampleConvertInterceptor.getSampleService() instanceof SampleService);
            }
        }
        assertTrue("The sample object finder should be in the list.", sampleConvertInterceptorFound);
    }

}

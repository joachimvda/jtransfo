/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.fest.assertions.Assertions.assertThat;

public class JTransfoSimpleTest {

    private static final String BLA = "something";

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private JTransfo jTransfo;

    @Before
    public void setup() {
        jTransfo = new JTransfoImpl();

        ConverterHelper converterHelper = new ConverterHelper() {
            @Override
            public ToConverter getToConverter(Class toClass, Class domainClass) {
                ToConverter converter = new ToConverter();
                converter.addToTo(new Converter() {
                    @Override
                    public void convert(Object source, Object target) {
                        ((SimpleClassNameTo) target).setBla(((SimpleClassDomain) source).getBla());
                    }
                });
                converter.addToDomain(new Converter() {
                    @Override
                    public void convert(Object source, Object target) {
                        ((SimpleClassDomain) target).setBla(((SimpleClassNameTo) source).getBla());
                    }
                });
                return converter;
            }
        };
        ReflectionTestUtils.setField(jTransfo, "converterHelper", converterHelper);
    }

    @Test
    public void testConvertDomainToTo() throws Exception {
        SimpleClassDomain source = new SimpleClassDomain();
        source.setBla(BLA);
        SimpleClassNameTo result = jTransfo.convert(source, new SimpleClassNameTo());
        assertThat(result).isInstanceOf(SimpleClassNameTo.class);
        assertThat(result.getBla()).isEqualTo(BLA);
    }

    @Test
    public void testConvertToToDomain() throws Exception {
        SimpleClassNameTo source = new SimpleClassNameTo();
        source.setBla(BLA);
        SimpleClassDomain result = jTransfo.convert(source, new SimpleClassDomain());
        assertThat(result).isInstanceOf(SimpleClassDomain.class);
        assertThat(result.getBla()).isEqualTo(BLA);
    }

    @Test
    public void testConvertSourceOnly() throws Exception {
        SimpleClassNameTo source = new SimpleClassNameTo();
        source.setBla(BLA);
        Object result = jTransfo.convert(source);
        assertThat(result).isInstanceOf(SimpleClassDomain.class);
        assertThat(((SimpleClassDomain) result).getBla()).isEqualTo(BLA);
    }

    @Test
    public void testConvertNullLeft() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Source and target are required to be not-null.");
        jTransfo.convert(null, new SimpleClassNameTo());
    }

    @Test
    public void testConvertNullRight() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Source and target are required to be not-null.");
        jTransfo.convert(new SimpleClassDomain(), null);
    }

    @Test
    public void testConvertNeedsTo() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Neither source nor target are annotated with DomainClass on classes " +
                "java.lang.Integer and java.lang.Double.");
        jTransfo.convert(1, 1.0);
    }

}

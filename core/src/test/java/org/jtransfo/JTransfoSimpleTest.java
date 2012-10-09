/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.fest.assertions.Assertions.assertThat;

public class JTransfoSimpleTest {

    private static final String BLA = "something";

    private JTransfo jTransfo;

    @Before
    public void setup() {
        jTransfo = new JTransfoImpl();

        ConverterHelper converterHelper = new ConverterHelper() {
            @Override
            public ToConverter getToConverter(Object to, Object domain) {
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

}
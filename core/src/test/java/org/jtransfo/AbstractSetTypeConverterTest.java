/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.SyntheticField;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractSetTypeConverterTest {

    private static final String NAME = "cname";

    AbstractSetTypeConverter setTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField field;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setTypeConverter = new AbstractSetTypeConverterTest.TestSetTypeConverter(NAME, AddressTo.class);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), anyObject())).thenReturn((Class) AddressTo.class);

        setTypeConverter.setJTransfo(jTransfo);
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(setTypeConverter.getName()).isEqualTo(NAME);
    }

    @Test
    public void testCanConvert() throws Exception {
        assertThat(setTypeConverter.canConvert(Set.class, Set.class)).isFalse();
    }

    @Test
    public void testConvert() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(2L);
        AddressTo to2 = new AddressTo();
        to2.setId(1L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);
        Object domainObject = new Object();
        Set resSet = spy(new HashSet());
        when(field.get(domainObject)).thenReturn(resSet);

        Set<AddressDomain> res = setTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field).get(domainObject);
        verify(resSet).clear();
    }

    @Test
    public void testConvertGetFails() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(2L);
        AddressTo to2 = new AddressTo();
        to2.setId(1L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);
        Object domainObject = new Object();
        Set resSet = spy(new HashSet());
        when(field.get(domainObject)).thenThrow(new NullPointerException());

        Set<AddressDomain> res = setTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field).get(domainObject);
    }

    @Test
    public void testConvertNewSet() throws Exception {
        setTypeConverter.setAlwaysNewSet(true);
        AddressTo to1 = new AddressTo();
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);

        Object domainObject = new Object();
        Set<AddressDomain> res = setTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field, times(0)).get(domainObject);

        res = setTypeConverter.convert(addresses, field, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field, times(0)).get(domainObject);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(setTypeConverter.convert(null, field, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.convert(null, field, null)).isNull();
    }

    @Test
    public void testReverse() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        Set<AddressDomain> addresses = new HashSet<>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        Set resSet = spy(new HashSet());
        when(field.get(toObject)).thenReturn(resSet);

        Set<AddressTo> res = setTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field).get(toObject);
        verify(resSet).clear();
    }

    @Test
    public void testReverseGetFails() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        Set<AddressDomain> addresses = new HashSet<>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        when(field.get(toObject)).thenThrow(new NullPointerException());

        Set<AddressTo> res = setTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res).extracting("id").contains(1L, 2L);
        verify(field).get(toObject);
    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(setTypeConverter.reverse(null, field, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.reverse(null, field, null)).isNull();
    }

    private class TestSetTypeConverter extends AbstractSetTypeConverter {

        private TestSetTypeConverter(String name, Class<?> toType) {
            super(name, toType);
        }

        @Override
        public Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
                throws JTransfoException {

            if (toObject instanceof AddressTo) {
                long id = ((AddressTo) toObject).getId();
                AddressDomain address = new AddressDomain();
                address.setId(id);
                return address;
            }
            return null;
        }

        /**
         * Do the actual reverse conversion of one object.
         *
         * @param jTransfo jTransfo instance in use
         * @param domainObject domain object
         * @param toField field definition on the transfer object
         * @param toType configured to type for list
         * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
         * @return domain object
         * @throws JTransfoException oops, cannot convert
         */
        @Override
        public Object doReverseOne(JTransfo jTransfo, Object domainObject, SyntheticField toField, Class<?> toType, String... tags) throws JTransfoException {
            if (domainObject instanceof AddressDomain) {
                long id = ((AddressDomain) domainObject).getId();
                AddressTo address = new AddressTo();
                address.setId(id);
                return address;
            }
            return null;
        }
    }

}
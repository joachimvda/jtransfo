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

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractListTypeConverterTest {

    private static final String NAME = "cname";

    AbstractListTypeConverter listTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField field;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        listTypeConverter = new TestListTypeConverter(NAME, AddressTo.class);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), anyObject())).thenReturn((Class) AddressTo.class);

        listTypeConverter.setJTransfo(jTransfo);
    }

    @Test
    public void testGetName() throws Exception {
        assertThat(listTypeConverter.getName()).isEqualTo(NAME);
    }

    @Test
    public void testCanConvert() throws Exception {
        assertThat(listTypeConverter.canConvert(List.class, List.class)).isFalse();
    }

    @Test
    public void testConvert() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(2L);
        AddressTo to2 = new AddressTo();
        to2.setId(1L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);
        Object domainObject = new Object();
        List resList = spy(new ArrayList());
        when(field.get(domainObject)).thenReturn(resList);

        List<AddressDomain> res = listTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(2L);
        assertThat(res.get(1).getId()).isEqualTo(1L);
        verify(field).get(domainObject);
        verify(resList).clear();
    }

    @Test
    public void testConvertGetFails() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(2L);
        AddressTo to2 = new AddressTo();
        to2.setId(1L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);
        Object domainObject = new Object();
        List resList = spy(new ArrayList());
        when(field.get(domainObject)).thenThrow(new NullPointerException());

        List<AddressDomain> res = listTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(2L);
        assertThat(res.get(1).getId()).isEqualTo(1L);
        verify(field).get(domainObject);
    }

    @Test
    public void testConvertSortedNotComparable() throws Exception {
        listTypeConverter.setSortList(true);
        AddressTo to1 = new AddressTo();
        to1.setId(2L);
        AddressTo to2 = new AddressTo();
        to2.setId(1L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);

        Object domainObject = new Object();
        List<AddressDomain> res = listTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(2L);
        assertThat(res.get(1).getId()).isEqualTo(1L);
        verify(field).get(domainObject);
    }

    @Test
    public void testConvertSortedComparable() throws Exception {
        listTypeConverter.setSortList(true);
        AddressTo to1 = new AddressTo();
        to1.setId(12L);
        AddressTo to2 = new AddressTo();
        to2.setId(11L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);

        Object domainObject = new Object();
        List<AddressDomain> res = listTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(11L);
        assertThat(res.get(1).getId()).isEqualTo(12L);
        verify(field).get(domainObject);
    }

    @Test
    public void testConvertNewList() throws Exception {
        listTypeConverter.setAlwaysNewList(true);
        AddressTo to1 = new AddressTo();
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);

        Object domainObject = new Object();
        List<AddressDomain> res = listTypeConverter.convert(addresses, field, domainObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(1L);
        assertThat(res.get(1).getId()).isEqualTo(2L);
        verify(field, times(0)).get(domainObject);

        res = listTypeConverter.convert(addresses, field, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(1L);
        assertThat(res.get(1).getId()).isEqualTo(2L);
        verify(field, times(0)).get(domainObject);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(listTypeConverter.convert(null, field, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.convert(null, field, null)).isNull();
    }

    @Test
    public void testReverse() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        List<AddressDomain> addresses = new ArrayList<AddressDomain>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        List resList = spy(new ArrayList());
        when(field.get(toObject)).thenReturn(resList);

        List<AddressTo> res = listTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        verify(field).get(toObject);
        verify(resList).clear();
    }

    @Test
    public void testReverseGetFails() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        List<AddressDomain> addresses = new ArrayList<AddressDomain>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        when(field.get(toObject)).thenThrow(new NullPointerException());

        List<AddressTo> res = listTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        verify(field).get(toObject);
    }

    @Test
    public void testReverseSortNotComparable() throws Exception {
        listTypeConverter.setSortList(true);
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(2L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(1L);
        List<AddressDomain> addresses = new ArrayList<AddressDomain>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        List resList = spy(new ArrayList());
        when(field.get(toObject)).thenReturn(resList);
        when(jTransfo.convertTo(ad1, AddressTo.class)).thenReturn(new AddressTo(2L));
        when(jTransfo.convertTo(ad2, AddressTo.class)).thenReturn(new AddressTo(1L));

        List<AddressTo> res = listTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(2L);
        assertThat(res.get(1).getId()).isEqualTo(1L);
        verify(field).get(toObject);
        verify(resList).clear();
    }

    @Test
    public void testReverseSortComparable() throws Exception {
        listTypeConverter.setSortList(true);
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(2L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(1L);
        List<AddressDomain> addresses = new ArrayList<AddressDomain>();
        addresses.add(ad1);
        addresses.add(ad2);
        Object toObject = new Object();
        List resList = spy(new ArrayList());
        when(field.get(toObject)).thenReturn(resList);
        when(jTransfo.convertTo(ad1, AddressTo.class)).thenReturn(new ComparableAddressTo(2L));
        when(jTransfo.convertTo(ad2, AddressTo.class)).thenReturn(new ComparableAddressTo(1L));

        List<AddressTo> res = listTypeConverter.reverse(addresses, field, toObject);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        assertThat(res.get(0).getId()).isEqualTo(1L);
        assertThat(res.get(1).getId()).isEqualTo(2L);
        verify(field).get(toObject);
        verify(resList).clear();
    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(listTypeConverter.reverse(null, field, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.reverse(null, field, null)).isNull();
    }

    private class TestListTypeConverter extends AbstractListTypeConverter {

        private TestListTypeConverter(String name, Class<?> toType) {
            super(name, toType);
        }

        @Override
        public Object doConvertOne(JTransfo jTransfo, Object toObject, Class<?> domainObjectType, String... tags)
                throws JTransfoException {

            if (toObject instanceof AddressTo) {
                long id = ((AddressTo) toObject).getId();
                AddressDomain address = id > 10 ? new ComparableAddressDomain() : new AddressDomain();
                address.setId(id);
                return address;
            }
            return null;
        }
    }

    private class ComparableAddressDomain extends AddressDomain implements Comparable<ComparableAddressDomain> {
        public int compareTo(ComparableAddressDomain comparableAddressDomain) {
            return (int) (getId() - comparableAddressDomain.getId());
        }
    }

    private class ComparableAddressTo extends AddressTo implements Comparable<ComparableAddressTo> {
        private ComparableAddressTo(Long id) {
            super(id);
        }

        public int compareTo(ComparableAddressTo comparableAddressTo) {
            return (int) (getId() - comparableAddressTo.getId());
        }
    }
}

package org.jtransfo;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for ListTypeConverter.
 */
public class ListTypeConverterTest {

    private static final String NAME = "cname";

    ListTypeConverter listTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        listTypeConverter = new ListTypeConverter(NAME, AddressTo.class);

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
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        List<AddressTo> addresses = new ArrayList<AddressTo>();
        addresses.add(to1);
        addresses.add(to2);

        List<AddressDomain> res = listTypeConverter.convert(addresses, List.class);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convert(to1);
        verify(jTransfo).convert(to2);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(listTypeConverter.convert(null, List.class)).isNull();
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

        List<AddressTo> res = listTypeConverter.reverse(addresses, List.class);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);

    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(listTypeConverter.reverse(null, List.class)).isNull();
    }
}

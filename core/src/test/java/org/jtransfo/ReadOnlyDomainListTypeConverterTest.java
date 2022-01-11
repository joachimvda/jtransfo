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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for ReadOnlyDomainListTypeConverter.
 */
public class ReadOnlyDomainListTypeConverterTest {

    private static final String NAME = "cname";

    ReadOnlyDomainListTypeConverter listTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField listField;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        listTypeConverter = new ReadOnlyDomainListTypeConverter(NAME, AddressTo.class);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), any())).thenReturn((Class) AddressTo.class);

        listTypeConverter.setJTransfo(jTransfo);

        when(listField.getType()).thenReturn((Class) List.class);
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
        List<AddressTo> addresses = new ArrayList<>();
        addresses.add(to1);
        addresses.add(to2);

        List<AddressDomain> res = listTypeConverter.convert(addresses, listField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).findTarget(to1, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
        verify(jTransfo).findTarget(to2, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(listTypeConverter.convert(null, listField, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.convert(null, listField, null)).isNull();
    }

    @Test
    public void testReverse() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        List<AddressDomain> addresses = new ArrayList<>();
        addresses.add(ad1);
        addresses.add(ad2);

        List<AddressTo> res = listTypeConverter.reverse(addresses, listField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);

    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(listTypeConverter.reverse(null, listField, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.reverse(null, listField, null)).isNull();
    }
}

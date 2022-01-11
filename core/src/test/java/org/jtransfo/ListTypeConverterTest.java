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
 * Test for ListTypeConverter.
 */
public class ListTypeConverterTest {

    private static final String NAME = "cname";

    ListTypeConverter listTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField field;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        listTypeConverter = new ListTypeConverter(NAME, AddressTo.class);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), any())).thenReturn((Class) AddressTo.class);

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
        List<AddressTo> addresses = new ArrayList<>();
        addresses.add(to1);
        addresses.add(to2);
        String[] tags = new String[] { "a", "b", "c" };

        List<AddressDomain> res = listTypeConverter.convert(addresses, field, null, tags);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(to1, AddressDomain.class, tags);
        verify(jTransfo).convertTo(to2, AddressDomain.class, tags);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(listTypeConverter.convert(null, field, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.convert(null, field, null)).isNull();

        List<AddressTo> listWithNull = new ArrayList<>();
        listWithNull.add(null);
        assertThat(listTypeConverter.convert(listWithNull, field, null)).isEqualTo(listWithNull);
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

        List<AddressTo> res = listTypeConverter.reverse(addresses, field, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);

    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(listTypeConverter.reverse(null, field, null)).isEmpty();
        listTypeConverter.setKeepNullList(true);
        assertThat(listTypeConverter.reverse(null, field, null)).isNull();
    }
}

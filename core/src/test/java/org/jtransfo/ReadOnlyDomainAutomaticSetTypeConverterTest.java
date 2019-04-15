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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for ReadOnlyDomainAutomaticSetTypeConverterTest.
 */
public class ReadOnlyDomainAutomaticSetTypeConverterTest {

    private static final String NAME = "cname";

    ReadOnlyDomainAutomaticSetTypeConverter setTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField setField;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setTypeConverter = new ReadOnlyDomainAutomaticSetTypeConverter(NAME);
        setTypeConverter.setJTransfo(jTransfo);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), anyObject())).thenReturn((Class) AddressTo.class);

        when(setField.getType()).thenReturn((Class) Set.class);
        when(setField.getGenericType()).thenReturn(AddressSetContainer.class.getField("addressToSet").getGenericType());
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
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);

        Set<AddressDomain> res = setTypeConverter.convert(addresses, setField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(1);
        verify(jTransfo).findTarget(to1, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
        verify(jTransfo).findTarget(to2, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(setTypeConverter.convert(null, setField, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.convert(null, setField, null)).isNull();
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

        Set<AddressTo> res = setTypeConverter.reverse(addresses, setField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(1);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);

    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(setTypeConverter.reverse(null, setField, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.reverse(null, setField, null)).isNull();
    }

    class AddressSetContainer {
        public Set<AddressTo> addressToSet;
    }
}

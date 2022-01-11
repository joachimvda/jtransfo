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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for ListTypeConverter.
 */
public class SetTypeConverterTest {

    private static final String NAME = "cname";

    SetTypeConverter setTypeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private SyntheticField field;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        setTypeConverter = new SetTypeConverter(NAME, AddressTo.class);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), any())).thenReturn((Class) AddressTo.class);

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
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);
        String[] tags = new String[] { "a", "b", "c" };
        when(jTransfo.convertTo(to1, AddressDomain.class, tags)).thenReturn(new AddressDomain().withId(1L));
        when(jTransfo.convertTo(to2, AddressDomain.class, tags)).thenReturn(new AddressDomain().withId(2L));

        Set<AddressDomain> res = setTypeConverter.convert(addresses, field, null, tags);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(to1, AddressDomain.class, tags);
        verify(jTransfo).convertTo(to2, AddressDomain.class, tags);
    }

    @Test
    public void testConvertNull() throws Exception {
        assertThat(setTypeConverter.convert(null, field, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.convert(null, field, null)).isNull();

        Set<AddressTo> setWithNull = new HashSet<>();
        setWithNull.add(null);
        assertThat(setTypeConverter.convert(setWithNull, field, null)).isEqualTo(setWithNull);
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
        String[] tags = new String[] { "x", "y"};
        when(jTransfo.convertTo(ad1, AddressTo.class, tags)).thenReturn(new AddressTo().withId(1L));
        when(jTransfo.convertTo(ad2, AddressTo.class, tags)).thenReturn(new AddressTo().withId(2L));


        Set<AddressTo> res = setTypeConverter.reverse(addresses, field, null, tags);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class, tags);
        verify(jTransfo).convertTo(ad2, AddressTo.class, tags);

    }

    @Test
    public void testReverseNull() throws Exception {
        assertThat(setTypeConverter.reverse(null, field, null)).isEmpty();
        setTypeConverter.setKeepNullSet(true);
        assertThat(setTypeConverter.reverse(null, field, null)).isNull();
    }

}

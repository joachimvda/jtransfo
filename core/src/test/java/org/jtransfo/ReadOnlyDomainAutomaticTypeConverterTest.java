package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.SyntheticField;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.object.SimpleBaseDomain;
import org.jtransfo.object.SimpleBaseTo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for converter for fields which are themselves transfer objects..
 */
@RunWith(MockitoJUnitRunner.class)
public class ReadOnlyDomainAutomaticTypeConverterTest {

    private ReadOnlyDomainAutomaticTypeConverter typeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Mock
    private SyntheticField toField;

    @Mock
    private SyntheticField domainField;

    @Mock
    private SyntheticField listField;

    @Mock
    private SyntheticField setField;

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Before
    public void setUp() throws Exception {
        typeConverter = new ReadOnlyDomainAutomaticTypeConverter();
        typeConverter.setJTransfo(jTransfo);

        when(jTransfo.getDomainClass(AddressTo.class)).thenReturn((Class) AddressDomain.class);
        when(jTransfo.getToSubType(eq(AddressTo.class), any())).thenReturn((Class) AddressTo.class);

        when(listField.getType()).thenReturn((Class) List.class);
        when(listField.getGenericType()).thenReturn(ReadOnlyDomainAutomaticListTypeConverterTest.AddressListContainer.class.getField("addressToList").getGenericType());

        when(setField.getType()).thenReturn((Class) Set.class);
        when(setField.getGenericType()).thenReturn(ReadOnlyDomainAutomaticSetTypeConverterTest.AddressSetContainer.class.getField("addressToSet").getGenericType());

        ReflectionTestUtils.setField(typeConverter, "reflectionHelper", reflectionHelper);

        when(jTransfo.isToClass(SimpleBaseTo.class)).thenReturn(true);
        when(jTransfo.isToClass(SimpleExtendedTo.class)).thenReturn(true);
        when(jTransfo.getDomainClass(SimpleBaseTo.class)).thenReturn((Class) SimpleBaseDomain.class);
        when(jTransfo.getDomainClass(SimpleExtendedTo.class)).thenReturn((Class) SimpleExtendedDomain.class);
        when(jTransfo.getToSubType(eq(SimpleBaseTo.class), any())).thenReturn((Class) SimpleBaseTo.class);
        when(domainField.getType()).thenReturn((Class) SimpleBaseDomain.class);
        when(toField.getType()).thenReturn((Class) SimpleBaseTo.class);
    }

    @Test
    public void testCanConvertSimple() throws Exception {
        assertThat(typeConverter.canConvert(String.class, String.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, SimpleBaseDomain.class)).isFalse(); // can convert but needs to be declared
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, String.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, SimpleExtendedDomain.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleExtendedTo.class, SimpleExtendedDomain.class)).isFalse(); // can convert but needs to be declared
        assertThat(typeConverter.canConvert(SimpleExtendedTo.class, SimpleBaseDomain.class)).isFalse(); // can convert but needs to be declared
    }

    @Test
    public void testConvertSimple() throws Exception {
        SimpleBaseTo source = new SimpleBaseTo();
        typeConverter.convert(source, domainField, null);

        verify(jTransfo).findTarget(source, SimpleBaseDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
        verify(jTransfo).getDomainClass(SimpleBaseTo.class);

        assertThat(typeConverter.convert(source, domainField, null)).isNull();
    }


    @Test
    public void testConvertNull() throws Exception {
        assertThat(typeConverter.convert(null, domainField, null)).isNull();
    }

    @Test
    public void testReverseSimple() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenReturn(target);
        typeConverter.reverse(source, toField, null);

        verify(jTransfo).convert(source, target);
    }

    @Test
    public void testReverseNullHandling() throws Exception {
        assertThat(typeConverter.reverse(null, toField, null)).isNull();
    }

    @Test
    public void testReverseInstantiationException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, toField, null);
    }

    @Test
    public void testReverseIllegalAccessException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new IllegalAccessException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, toField, null);
    }

    @Test
    public void testName() throws Exception {
        assertThat(typeConverter.getName()).isEqualTo("readOnlyDomain");

        typeConverter.setName("bla");
        assertThat(typeConverter.getName()).isEqualTo("bla");
    }

    @Test
    public void testCanConvertList() throws Exception {
        assertThat(typeConverter.canConvert(List.class, List.class)).isFalse();
    }

    @Test
    public void testCanConvertSet() throws Exception {
        assertThat(typeConverter.canConvert(Set.class, Set.class)).isFalse();
    }

    @Test
    public void testConvertList() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        List<AddressTo> addresses = new ArrayList<>();
        addresses.add(to1);
        addresses.add(to2);

        List<AddressDomain> res = (List) typeConverter.convert(addresses, listField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).findTarget(to1, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
        verify(jTransfo).findTarget(to2, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
    }

    @Test
    public void testConvertSet() throws Exception {
        AddressTo to1 = new AddressTo();
        to1.setId(1L);
        AddressTo to2 = new AddressTo();
        to2.setId(2L);
        Set<AddressTo> addresses = new HashSet<>();
        addresses.add(to1);
        addresses.add(to2);

        Set<AddressDomain> res = (Set) typeConverter.convert(addresses, setField, null);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(1);
        verify(jTransfo).findTarget(to1, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
        verify(jTransfo).findTarget(to2, AddressDomain.class, JTransfo.TAG_WHEN_READ_ONLY_DOMAIN);
    }

    @Test
    public void testConvertNullList() throws Exception {
        assertThat((List) typeConverter.convert(null, listField, null)).isEmpty();
        typeConverter.setKeepNullList(true);
        assertThat(typeConverter.convert(null, listField, null)).isNull();
    }

    @Test
    public void testConvertNullSet() throws Exception {
        assertThat((Set) typeConverter.convert(null, setField, null)).isEmpty();
        typeConverter.setKeepNullSet(true);
        assertThat(typeConverter.convert(null, setField, null)).isNull();
    }

    @Test
    public void testReverseList() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        List<AddressDomain> addresses = new ArrayList<>();
        addresses.add(ad1);
        addresses.add(ad2);
        List<AddressTo> resList = new ArrayList<>();
        Object to = new Object();
        when(listField.get(to)).thenReturn(resList);

        List<AddressTo> res = (List) typeConverter.reverse(addresses, listField, to);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        assertThat(res).isEqualTo(resList);
    }

    @Test
    public void testReverseSet() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        Set<AddressDomain> addresses = new HashSet<>();
        addresses.add(ad1);
        addresses.add(ad2);
        Set<AddressTo> resList = new HashSet<>();
        Object to = new Object();
        when(setField.get(to)).thenReturn(resList);

        Set<AddressTo> res = (Set) typeConverter.reverse(addresses, setField, to);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(1);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        assertThat(res).isEqualTo(resList);
    }

    @Test
    public void testReverseList_new() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        List<AddressDomain> addresses = new ArrayList<>();
        addresses.add(ad1);
        addresses.add(ad2);
        List<AddressTo> resList = new ArrayList<>();
        Object to = new Object();
        when(listField.get(to)).thenReturn(resList);
        typeConverter.setAlwaysNewList(true);
        typeConverter.setSortList(true);

        List<AddressTo> res = (List) typeConverter.reverse(addresses, listField, to);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(2);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        assertThat(res).isNotEqualTo(resList);
    }

    @Test
    public void testReverseSet_new() throws Exception {
        AddressDomain ad1 = new AddressDomain();
        ad1.setId(1L);
        AddressDomain ad2 = new AddressDomain();
        ad2.setId(2L);
        Set<AddressDomain> addresses = new HashSet<>();
        addresses.add(ad1);
        addresses.add(ad2);
        Set<AddressTo> resList = new HashSet<>();
        Object to = new Object();
        when(setField.get(to)).thenReturn(resList);
        typeConverter.setAlwaysNewSet(true);

        Set<AddressTo> res = (Set) typeConverter.reverse(addresses, setField, to);

        assertThat(res).isNotNull();
        assertThat(res).hasSize(1);
        verify(jTransfo).convertTo(ad1, AddressTo.class);
        verify(jTransfo).convertTo(ad2, AddressTo.class);
        assertThat(res).isNotEqualTo(resList);
    }

    @Test
    public void testReverseNullList() throws Exception {
        assertThat((List) typeConverter.reverse(null, listField, null)).isEmpty();
        typeConverter.setKeepNullList(true);
        assertThat(typeConverter.reverse(null, listField, null)).isNull();
    }

    @Test
    public void testReverseNullSet() throws Exception {
        assertThat((Set) typeConverter.reverse(null, setField, null)).isEmpty();
        typeConverter.setKeepNullSet(true);
        assertThat(typeConverter.reverse(null, setField, null)).isNull();
    }

}

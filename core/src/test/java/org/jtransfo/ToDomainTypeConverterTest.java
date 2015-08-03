package org.jtransfo;

import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.SyntheticField;
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
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for converter for fields which are themselves transfer objects..
 */
@RunWith(MockitoJUnitRunner.class)
public class ToDomainTypeConverterTest {

    private TypeConverter typeConverter;

    @Mock
    private JTransfo jTransfo;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        typeConverter = new ToDomainTypeConverter(jTransfo);

        ReflectionTestUtils.setField(typeConverter, "reflectionHelper", reflectionHelper);

        when(jTransfo.isToClass(SimpleBaseTo.class)).thenReturn(true);
        when(jTransfo.isToClass(SimpleExtendedTo.class)).thenReturn(true);
        when(jTransfo.getDomainClass(SimpleBaseTo.class)).thenReturn((Class) SimpleBaseDomain.class);
        when(jTransfo.getDomainClass(SimpleExtendedTo.class)).thenReturn((Class) SimpleExtendedDomain.class);
        when(jTransfo.getToSubType(eq(SimpleBaseTo.class), anyObject())).thenReturn((Class) SimpleBaseTo.class);
    }

    @Test
    public void testCanConvert() throws Exception {
        assertThat(typeConverter.canConvert(String.class, String.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, SimpleBaseDomain.class)).isTrue();
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, String.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleBaseTo.class, SimpleExtendedDomain.class)).isFalse();
        assertThat(typeConverter.canConvert(SimpleExtendedTo.class, SimpleExtendedDomain.class)).isTrue();
        assertThat(typeConverter.canConvert(SimpleExtendedTo.class, SimpleBaseDomain.class)).isTrue();
    }

    @Test
    public void testConvert() throws Exception {
        SimpleBaseTo source = new SimpleBaseTo();
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseDomain.class);
        String[] tags = new String[] { "a", "b", "c" };
        typeConverter.convert(source, field, null, tags);

        verify(jTransfo).convertTo(source, SimpleBaseDomain.class, tags);
    }

    @Test
    public void testConvertNull() throws Exception {
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseDomain.class);
        String[] tags = new String[] { "a", "b", "c" };
        assertThat(typeConverter.convert(null, field, null, tags)).isNull();
    }

    @Test
    public void testReverse() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenReturn(target);
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseTo.class);
        String[] tags = new String[] { "a", "b", "c" };
        typeConverter.reverse(source, field, null, tags);

        verify(jTransfo).convert(source, target, tags);
    }

    @Test
    public void testReverseNullHandling() throws Exception {
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseTo.class);
        assertThat(typeConverter.reverse(null, field, null)).isNull();
    }

    @Test
    public void testReverseInstantiationException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new InstantiationException());
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseTo.class);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, field, null);
    }

    @Test
    public void testReverseIllegalAccessException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new IllegalAccessException());
        SyntheticField field = mock(SyntheticField.class);
        when(field.getType()).thenReturn((Class) SimpleBaseTo.class);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance of transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, field, null);
    }

}

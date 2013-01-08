package org.jtransfo;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.jtransfo.internal.ReflectionHelper;
import org.jtransfo.internal.ToHelper;
import org.jtransfo.object.SimpleBaseDomain;
import org.jtransfo.object.SimpleBaseTo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
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
    private ToHelper toHelper;

    @Mock
    private ReflectionHelper reflectionHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        typeConverter = new ToDomainTypeConverter(jTransfo, new ToHelper()); // mocking ToHelper does not work well

        ReflectionTestUtils.setField(typeConverter, "reflectionHelper", reflectionHelper);
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
        typeConverter.convert(source, SimpleBaseDomain.class);

        verify(jTransfo).convert(source);
    }

    @Test
    public void testReverse() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenReturn(target);
        typeConverter.reverse(source, SimpleBaseTo.class);

        verify(jTransfo).convert(source, target);
    }

    @Test
    public void testReverseNullHandling() throws Exception {
        assertThat(typeConverter.reverse(null, SimpleBaseTo.class)).isNull();
    }

    @Test
    public void testReverseInstantiationException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new InstantiationException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, SimpleBaseTo.class);
    }

    @Test
    public void testReverseIllegalAccessException() throws Exception {
        SimpleBaseDomain source = new SimpleBaseDomain();
        SimpleBaseTo target = new SimpleBaseTo();
        when(reflectionHelper.newInstance(any(Class.class))).thenThrow(new IllegalAccessException());

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot create instance for transfer object class org.jtransfo.object.SimpleBaseTo.");

        typeConverter.reverse(source, SimpleBaseTo.class);
    }

}

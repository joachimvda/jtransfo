package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.object.SimpleExtendedDomain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.reflect.Field;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for AbstractConverter.
 */
public class AbstractConverterTest {

    private int doConvertResult = 0;
    private AbstractConverter abstractConverter;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        abstractConverter = new TestConverter();
    }


    @Test
    public void testConvertNothing() throws Exception {
        abstractConverter.convert(null, null);
        // does not fail!
    }

    @Test
    public void testConvertJTransfoException() throws Exception {
        doConvertResult = 1;
        exception.expect(JTransfoException.class);
        exception.expectMessage("jte");
        abstractConverter.convert(null, null);
    }

    @Test
    public void testConvertIllegalAccessException() throws Exception {
        doConvertResult = 2;
        exception.expect(JTransfoException.class);
        exception.expectMessage("NoAccess");
        abstractConverter.convert(null, null);
    }

    @Test
    public void testConvertIllegalArgumentException() throws Exception {
        doConvertResult = 3;
        exception.expect(JTransfoException.class);
        exception.expectMessage("CannotConvert");
        abstractConverter.convert(null, null);
    }

    @Test
    public void testDomainFieldName() throws Exception {
        assertThat(abstractConverter.domainFieldName(new Field[]{
                SimpleExtendedDomain.class.getDeclaredField("i")})).isEqualTo("i");
        assertThat(abstractConverter.domainFieldName(new Field[]{
                SimpleExtendedDomain.class.getDeclaredField("c"),
                SimpleExtendedDomain.class.getDeclaredField("i")})).isEqualTo("i (with path c)");
        assertThat(abstractConverter.domainFieldName(new Field[]{
                SimpleExtendedDomain.class.getDeclaredField("b"),
                SimpleExtendedDomain.class.getDeclaredField("c"),
                SimpleExtendedDomain.class.getDeclaredField("i")})).isEqualTo("i (with path b.c)");
        assertThat(abstractConverter.domainFieldName(new Field[]{})).isEqualTo("");
    }

    private final class TestConverter extends AbstractConverter {
        @Override
        public void doConvert(Object source, Object target)
        throws JTransfoException, IllegalAccessException, IllegalArgumentException {
            switch (doConvertResult) {
                case 0:
                    break;
                case 1:
                    throw new JTransfoException("jte");
                case 2:
                    throw new IllegalAccessException("access");
                case 3:
                    throw new IllegalArgumentException("argument");
            }
        }

        @Override
        public String accessExceptionMessage() {
            return "NoAccess";
        }

        @Override
        public String argumentExceptionMessage() {
            return "CannotConvert";
        }
    }
}

package org.jtransfo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Test for "clear" exception message for IllegalArgumentException during conversion.
 */
public class IllegalArgumentExceptionTest {

    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        jTransfo = new JTransfoImpl();
    }

    @Test
    public void TestIllegalArgumentException_framework() {
        IaeValueTo to = new IaeValueTo();
        to.setValue("123");

        exception.expect(JTransfoException.class);
        exception.expectMessage("Cannot convert TO field value to domain field value, field needs type conversion.");

        jTransfo.convert(to);
    }

    @Test
    public void TestIllegalArgumentException_user() {
        IaeSmallValueTo to = new IaeSmallValueTo();
        to.setSmallValue(2);

        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Value should be small!");

        jTransfo.convert(to);
    }

    public static class IaeDomain {
        private int value;
        private int smallValue;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int getSmallValue() {
            return smallValue;
        }

        public void setSmallValue(int value) {
            if (value > 1) {
                throw new IllegalArgumentException("Value should be small!");
            }
            this.smallValue = value;
        }
    }

    @DomainClass(domainClass = IaeDomain.class)
    private static class IaeValueTo {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @DomainClass(domainClass = IaeDomain.class)
    private static class IaeSmallValueTo {
        private int smallValue;

        public int getSmallValue() {
            return smallValue;
        }

        public void setSmallValue(int value) {
            this.smallValue = value;
        }
    }
}

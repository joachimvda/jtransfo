package org.jtransfo;

import org.jtransfo.object.BooleanDomain;
import org.jtransfo.object.BooleanTo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test to verify correct handling of all kinds of boolean fields, getter and setters, including support for hasXxx.
 */
public class BooleanConversionTest {

    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        jTransfo = new JTransfoImpl();
    }

    @Test
    public void testToTo() throws Exception {
        BooleanDomain domain = new BooleanDomain();
        domain.setBoolean1(true);
        domain.setBoolean2(true);
        domain.setHasBoolean3(true);
        domain.setBoolean4(true);
        domain.setIsBoolean6(true);

        BooleanTo res = new BooleanTo();
        jTransfo.convert(domain, res);

        assertThat(res.isBoolean1()).isTrue();
        assertThat(res.isBoolean2()).isTrue();
        assertThat(res.isHasBoolean3()).isTrue();
        assertThat(res.isBoolean4()).isTrue();
        assertThat(res.isBoolean5()).isTrue();
        assertThat(res.isBoolean6()).isTrue();
    }

    @Test
    public void testToDomain() throws Exception {
        BooleanTo to = new BooleanTo();
        to.setBoolean1(true);
        to.setBoolean2(true);
        to.setHasBoolean3(true);
        to.setBoolean4(true);
        to.setBoolean5(true);
        to.setBoolean6(true);

        BooleanDomain res = (BooleanDomain) jTransfo.convert(to);
        assertThat(res.isBoolean1()).isTrue();
        assertThat(res.isBoolean2()).isTrue();
        assertThat(res.isHasBoolean3()).isTrue();
        assertThat(res.hasBoolean4()).isTrue();
        assertThat(res.isBoolean5()).isTrue();
        assertThat(res.isIsBoolean6()).isTrue();
    }

}

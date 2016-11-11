package org.jtransfo;

import org.jtransfo.object.Gender;
import org.jtransfo.object.MalePersonTo;
import org.jtransfo.object.PersonDomain;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify correct handling of static fields. These should not be mapped by default but can be used to inject
 * default values.
 */
public class StaticFieldConversionTest {

    private JTransfo jTransfo;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        ConfigurableJTransfo impl = JTransfoFactory.get();
        jTransfo = impl;
        impl.getTypeConverters().add(new StringEnumTypeConverter(Gender.class));
        impl.updateTypeConverters();
    }

    @Test
    public void testToDomain() throws Exception {
        MalePersonTo person = new MalePersonTo();
        person.setName("Joske Vermeulen");

        PersonDomain res = (PersonDomain) jTransfo.convert(person);
        assertThat(res.getName()).isEqualTo("Joske Vermeulen");
        assertThat(res.getGender()).isEqualTo(Gender.MALE);
    }

    @Test
    public void testToTo() throws Exception {
        PersonDomain person = new PersonDomain();
        person.setName("Joske Vermeulen");
        person.setGender(Gender.FEMALE);

        MalePersonTo res = jTransfo.convertTo(person, MalePersonTo.class);
        assertThat(res.getName()).isEqualTo("Joske Vermeulen");
    }

}

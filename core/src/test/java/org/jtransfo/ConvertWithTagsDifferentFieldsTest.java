package org.jtransfo;

import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.Gender;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.SecretsTo;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that only allowed fields are copied when using {@link @MapOnly} annotations.
 */
public class ConvertWithTagsDifferentFieldsTest {

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        JTransfoImpl impl = new JTransfoImpl();
        jTransfo = impl;

        impl.getTypeConverters().add(new AutomaticStringEnumTypeConverter());
        impl.updateTypeConverters();
    }

    @Test
    public void  testConverterToDomain() throws Exception {
        SecretsTo to = new SecretsTo();
        to.setAddressOrName("Kerkstraat");
        to.setGenderOrName(Gender.FEMALE.name());

        PersonDomain domain;
        domain = jTransfo.convert(to, personDomainWithAddress(), "zzz");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(domain.getAddress().getId()).isNull();
        assertThat(domain.getAddress().getAddress()).isNull();

        domain = jTransfo.convert(to, personDomainWithAddress(), "name");
        assertThat(domain.getName()).isEqualTo("Kerkstraat"); // field defined last, will be "FEMALE" when changing order of fields
        assertThat(domain.getGender()).isEqualTo(Gender.FEMALE); // "*" case is always converted
        assertThat(domain.getAddress().getId()).isNull();
        assertThat(domain.getAddress().getAddress()).isNull();

        domain = jTransfo.convert(to, personDomainWithAddress(), "address");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(domain.getAddress().getId()).isNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Kerkstraat");
    }

    @Test
    public void  testConverterToTo() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName("Joske");
        domain.setGender(Gender.MALE);
        AddressDomain address = new AddressDomain();
        address.setId(7L);
        address.setAddress("Kerkstraat");
        domain.setAddress(address);

        SecretsTo to;
        to = jTransfo.convert(domain, new SecretsTo(), "zzz");
        assertThat(to.getAddressOrName()).isNull();
        assertThat(to.getGenderOrName()).isEqualTo("MALE");

        to = jTransfo.convert(domain, new SecretsTo(), "name");
        assertThat(to.getAddressOrName()).isEqualTo("Joske");
        assertThat(to.getGenderOrName()).isEqualTo("Joske");

        to = jTransfo.convert(domain, new SecretsTo(), "address");
        assertThat(to.getAddressOrName()).isEqualTo("Kerkstraat");
        assertThat(to.getGenderOrName()).isEqualTo("MALE");
    }

    private PersonDomain personDomainWithAddress() {
        PersonDomain res = new PersonDomain();
        res.setAddress(new AddressDomain());
        return res;
    }

}

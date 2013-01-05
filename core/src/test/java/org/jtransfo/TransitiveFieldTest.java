package org.jtransfo;

import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonTransitiveTo;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test a conversion with transitive fields.
 */
public class TransitiveFieldTest {

    private static final String NAME = "ikke";

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        jTransfo = new JTransfoImpl();
    }

    @Test
    public void testTransitiveToDomain() throws Exception {
        PersonTransitiveTo to = new PersonTransitiveTo();
        to.setName(NAME);
        to.setAddressId(3L);
        to.setAddress("Address 3");

        PersonDomain domain = new PersonDomain();
        domain.setAddress(new AddressDomain()); // need to set linked value, automatic filling not supported yet
        domain = jTransfo.convert(to, domain);
        assertThat(domain.getName()).isEqualTo(NAME);
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
    }

    @Test
    public void  testTransitiveToTo() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName(NAME);
        Date now = new Date();
        AddressDomain address = new AddressDomain();
        address.setId(7L);
        address.setAddress("Kerkstraat");
        domain.setAddress(address);

        PersonTransitiveTo to = jTransfo.convert(domain, new PersonTransitiveTo());
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddressId()).isEqualTo(7L);
        assertThat(to.getAddress()).isEqualTo("Kerkstraat");
    }

}

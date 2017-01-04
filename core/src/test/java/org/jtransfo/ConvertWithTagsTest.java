package org.jtransfo;

import org.jtransfo.internal.SyntheticField;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.object.Gender;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.TaggedPersonTo;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test which verifies that only allowed fields are copied when using {@link @MapOnly} annotations.
 */
public class ConvertWithTagsTest {

    private static final String NAME = "ikke";

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        ConfigurableJTransfo impl = JTransfoFactory.get();
        jTransfo = impl;

        AddressDomain[] addresses = new AddressDomain[10];
        for (int i = 0 ; i < 10 ; i++) {
            AddressDomain ad = new AddressDomain();
            ad.setId(Long.valueOf(i));
            ad.setAddress("Address " + i);
            addresses[i] = ad;
        }

        impl.getTypeConverters().add(new StringEnumTypeConverter(Gender.class));
        impl.getTypeConverters().add(new Always2TypeConverter(addresses));
        impl.updateTypeConverters();
        impl.getObjectFinders().add(new AddressFinder(addresses));
        impl.updateObjectFinders();
    }

    @Test
    public void  testConverterToDomain() throws Exception {
        TaggedPersonTo to = new TaggedPersonTo();
        to.setGender("MALE");
        to.setLastChanged(new Date());
        to.setName(NAME);
        to.setAddress(new AddressTo(3L));

        PersonDomain domain;
        domain = jTransfo.convert(to, new PersonDomain(), "create");
        assertThat(domain.getName()).isEqualTo(NAME);
        assertThat(domain.getGender()).isEqualTo(Gender.MALE);
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
        assertThat(domain.getLastChanged()).isNull();

        domain = jTransfo.convert(to, new PersonDomain(), "update");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isNull();
        assertThat(domain.getAddress().getId()).isEqualTo(2L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 2");
        assertThat(domain.getLastChanged()).isNull();

        domain = jTransfo.convert(to, new PersonDomain(), "addressPolice");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isNull();
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
        assertThat(domain.getLastChanged()).isNull();

        domain = jTransfo.convert(to, new PersonDomain(), "update", "addressPolice");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isNull();
        assertThat(domain.getAddress().getId()).isEqualTo(3L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 3");
        assertThat(domain.getLastChanged()).isNull();

        domain = jTransfo.convert(to, new PersonDomain(), "addressPolice", "update");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isNull();
        assertThat(domain.getAddress().getId()).isEqualTo(2L);
        assertThat(domain.getAddress().getAddress()).isNotNull();
        assertThat(domain.getAddress().getAddress()).isEqualTo("Address 2");
        assertThat(domain.getLastChanged()).isNull();

        domain = jTransfo.convert(to, new PersonDomain(), "zzz");
        assertThat(domain.getName()).isNull();
        assertThat(domain.getGender()).isNull();
        assertThat(domain.getAddress()).isNull();
        assertThat(domain.getLastChanged()).isNull();
    }

    @Test
    public void  testWithFinderAndConverterToTo() throws Exception {
        PersonDomain domain = new PersonDomain();
        domain.setName(NAME);
        domain.setGender(Gender.MALE);
        Date now = new Date();
        domain.setLastChanged(now);
        AddressDomain address = new AddressDomain();
        address.setId(7L);
        address.setAddress("Kerkstraat");
        domain.setAddress(address);

        TaggedPersonTo to;
        to = jTransfo.convert(domain, new TaggedPersonTo(), "create");
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(7L);
        assertThat(to.getLastChanged()).isEqualTo(now);

        to = jTransfo.convert(domain, new TaggedPersonTo(), "update");
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(2L);
        assertThat(to.getLastChanged()).isEqualTo(now);

        to = jTransfo.convert(domain, new TaggedPersonTo(), "addressPolice");
        assertThat(to.getName()).isNull();
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(7L);
        assertThat(to.getLastChanged()).isEqualTo(now);

        to = jTransfo.convert(domain, new TaggedPersonTo(), "update", "addressPolice");
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(7L);
        assertThat(to.getLastChanged()).isEqualTo(now);

        to = jTransfo.convert(domain, new TaggedPersonTo(), "addressPolice", "update");
        assertThat(to.getName()).isEqualTo(NAME);
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNotNull();
        assertThat(to.getAddress().getId()).isEqualTo(2L);
        assertThat(to.getLastChanged()).isEqualTo(now);

        to = jTransfo.convert(domain, new TaggedPersonTo(), "zzz");
        assertThat(to.getName()).isNull();
        assertThat(to.getGender()).isEqualTo("MALE");
        assertThat(to.getAddress()).isNull();
        assertThat(to.getLastChanged()).isEqualTo(now);
    }

    private class AddressFinder implements ObjectFinder {

        private AddressDomain[] addresses;

        public AddressFinder(AddressDomain[] addresses) {
            this.addresses = addresses;
        }

        public <T> T getObject(Class<T> domainClass, Object to, String... tags) {
            if (domainClass.isAssignableFrom(AddressDomain.class) && to instanceof AddressTo) {
                return (T) addresses[((AddressTo) to).getId().intValue()];
            }
            return null;
        }
    }

    private class Always2TypeConverter implements TypeConverter<AddressTo, AddressDomain>, Named {
        private AddressDomain[] addresses;

        public Always2TypeConverter(AddressDomain[] addresses) {
            this.addresses = addresses;
        }

        public String getName() {
            return "always2";
        }

        public boolean canConvert(Type realToType, Type realDomainType) {
            return false;
        }

        public AddressDomain convert(AddressTo object, SyntheticField domainField, Object domainObject, String... tags)
                throws JTransfoException {
            return addresses[2];
        }

        public AddressTo reverse(AddressDomain object, SyntheticField toField, Object toObject, String... tags)
                throws JTransfoException {
            return new AddressTo(2L);
        }
    }
}

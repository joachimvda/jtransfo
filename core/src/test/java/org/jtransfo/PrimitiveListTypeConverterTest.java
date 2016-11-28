package org.jtransfo;

import org.jtransfo.object.HouseDomain;
import org.jtransfo.object.HouseTo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveListTypeConverterTest {

    private ConfigurableJTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        jTransfo = JTransfoFactory.get();
        AutomaticListTypeConverter listConverter = new AutomaticListTypeConverter(jTransfo);
        listConverter.setEmptyListSupplier(MyArrayList::new);
        jTransfo.with(listConverter);
    }

    @Test
    public void testConvertDomainObject() throws Exception {
        HouseDomain hd = new HouseDomain();
        hd.setPetNames(Arrays.asList("Garfield", "Odie"));

        HouseTo ht = jTransfo.convert(hd, new HouseTo());

        assertThat(ht.getPetNames()).containsExactly("Garfield", "Odie");
        assertThat(hd.getPetNames()).isNotInstanceOf(MyArrayList.class); // original is plain ArrayList
        assertThat(ht.getPetNames()).isInstanceOf(MyArrayList.class); // new copy used EmtyListSupplier
    }

    @Test
    public void testConvertTransferObject() throws Exception {
        HouseTo ht = new HouseTo();
        ht.setPetNames(Arrays.asList("Garfield", "Odie"));

        HouseDomain hd = jTransfo.convert(ht, new HouseDomain());

        assertThat(hd.getPetNames()).containsExactly("Garfield", "Odie");
        assertThat(ht.getPetNames()).isNotInstanceOf(MyArrayList.class); // original is plain ArrayList
        assertThat(hd.getPetNames()).isInstanceOf(MyArrayList.class); // new copy used EmtyListSupplier
    }

    private class MyArrayList extends ArrayList<String> {}

}

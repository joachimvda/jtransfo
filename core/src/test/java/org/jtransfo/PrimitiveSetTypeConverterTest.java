package org.jtransfo;

import org.jtransfo.object.FlatDomain;
import org.jtransfo.object.FlatTo;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveSetTypeConverterTest {

    private ConfigurableJTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        jTransfo = JTransfoFactory.get();
        AutomaticSetTypeConverter setTypeConverter = new AutomaticSetTypeConverter();
        setTypeConverter.setEmptySetSupplier(MyHashSet::new);
        jTransfo.with(setTypeConverter);
    }

    @Test
    public void testConvertDomainObject() throws Exception {
        FlatDomain hd = new FlatDomain();
        HashSet<String> pets = new HashSet<>();
        pets.addAll(Arrays.asList("Garfield", "Odie"));
        hd.setPetNames(pets);

        FlatTo ht = jTransfo.convert(hd, new FlatTo());

        assertThat(ht.getPetNames()).containsExactly("Garfield", "Odie");
        assertThat(hd.getPetNames()).isNotInstanceOf(MyHashSet.class); // original is plain HashSet
        assertThat(ht.getPetNames()).isInstanceOf(MyHashSet.class); // new copy used EmptySetSupplier
    }

    @Test
    public void testConvertTransferObject() throws Exception {
        FlatTo ht = new FlatTo();
        HashSet<String> pets = new HashSet<>();
        pets.addAll(Arrays.asList("Garfield", "Odie"));
        ht.setPetNames(pets);

        FlatDomain hd = jTransfo.convert(ht, new FlatDomain());

        assertThat(hd.getPetNames()).containsExactly("Garfield", "Odie");
        assertThat(ht.getPetNames()).isNotInstanceOf(MyHashSet.class); // original is plain HashSet
        assertThat(hd.getPetNames()).isInstanceOf(MyHashSet.class); // new copy used EmptySetSupplier
    }

    private class MyHashSet extends HashSet<String> {}

}

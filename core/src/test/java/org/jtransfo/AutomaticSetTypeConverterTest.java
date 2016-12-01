package org.jtransfo;

import org.jtransfo.object.AbstractHumanDomain;
import org.jtransfo.object.AbstractHumanTo;
import org.jtransfo.object.FemaleHumanDomain;
import org.jtransfo.object.FemaleHumanTo;
import org.jtransfo.object.FlatDomain;
import org.jtransfo.object.FlatTo;
import org.jtransfo.object.MaleHumanDomain;
import org.jtransfo.object.MaleHumanTo;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomaticSetTypeConverterTest {

    private ConfigurableJTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        jTransfo = JTransfoFactory.get();
        jTransfo.with(new AutomaticSetTypeConverter());
    }

    @Test
    public void testCanConvert() throws Exception {
        AutomaticSetTypeConverter typeConverter = new AutomaticSetTypeConverter(jTransfo);

        assertThat(typeConverter.canConvert(Set.class, Set.class)).isFalse();
    }

    @Test
    public void testConvertDomainObject() throws Exception {
        FlatDomain hd = new FlatDomain();
        MaleHumanDomain owner = new MaleHumanDomain();
        owner.setName("the owner");
        owner.setWeeklyPubVisits(4);
        MaleHumanDomain id1 = new MaleHumanDomain();
        id1.setName("male inhabitant");
        id1.setWeeklyPubVisits(0);
        FemaleHumanDomain id2 = new FemaleHumanDomain();
        id2.setName("female inhabitant");
        id2.setHairColourCount(1);
        Set<AbstractHumanDomain> inhabitants = new HashSet<>();
        inhabitants.add(id1);
        inhabitants.add(id2);
        hd.setOwner(owner);
        hd.setInhabitants(inhabitants);

        FlatTo ht = jTransfo.convert(hd, new FlatTo());

        assertThat(ht.getOwner()).isNotNull();
        assertThat(ht.getOwner()).isInstanceOf(MaleHumanTo.class);
        assertThat(ht.getOwner().getName()).isEqualTo("the owner");
        assertThat(((MaleHumanTo) ht.getOwner()).getWeeklyPubVisits()).isEqualTo(4);

        assertThat(ht.getInhabitants()).hasSize(2).
                extracting("name").contains("male inhabitant", "female inhabitant");
    }

    @Test
    public void testConvertTransferObject() throws Exception {
        FlatTo ht = new FlatTo();
        MaleHumanTo owner = new MaleHumanTo();
        owner.setName("the owner");
        owner.setWeeklyPubVisits(4);
        MaleHumanTo it1 = new MaleHumanTo();
        it1.setName("male inhabitant");
        it1.setWeeklyPubVisits(0);
        FemaleHumanTo it2 = new FemaleHumanTo();
        it2.setName("female inhabitant");
        it2.setHairColourCount(1);
        Set<AbstractHumanTo> inhabitants = new HashSet<>();
        inhabitants.add(it1);
        inhabitants.add(it2);
        ht.setOwner(owner);
        ht.setInhabitants(inhabitants);

        FlatDomain hd = jTransfo.convert(ht, new FlatDomain());

        assertThat(hd.getOwner()).isNotNull();
        assertThat(hd.getOwner()).isInstanceOf(MaleHumanDomain.class);
        assertThat(hd.getOwner().getName()).isEqualTo("the owner");
        assertThat(((MaleHumanDomain) hd.getOwner()).getWeeklyPubVisits()).isEqualTo(4);

        assertThat(hd.getInhabitants()).hasSize(2).
                extracting("name").contains("male inhabitant", "female inhabitant");
    }

}

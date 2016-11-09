package org.jtransfo;

import org.jtransfo.object.AbstractHumanDomain;
import org.jtransfo.object.AbstractHumanTo;
import org.jtransfo.object.FemaleHumanDomain;
import org.jtransfo.object.FemaleHumanTo;
import org.jtransfo.object.HouseDomain;
import org.jtransfo.object.HouseTo;
import org.jtransfo.object.MaleHumanDomain;
import org.jtransfo.object.MaleHumanTo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AutomaticListTypeConverterTest {

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        jTransfo = new JTransfoImpl();
        ((JTransfoImpl) jTransfo).getTypeConverters().add(new AutomaticListTypeConverter());
        ((JTransfoImpl) jTransfo).updateTypeConverters();
    }

    @Test
    public void testCanConvert() throws Exception {
        AutomaticListTypeConverter typeConverter = new AutomaticListTypeConverter(jTransfo);

        assertThat(typeConverter.canConvert(List.class, List.class)).isFalse();
    }

    @Test
    public void testConvertDomainObject() throws Exception {
        HouseDomain hd = new HouseDomain();
        MaleHumanDomain owner = new MaleHumanDomain();
        owner.setName("the owner");
        owner.setWeeklyPubVisits(4);
        MaleHumanDomain id1 = new MaleHumanDomain();
        id1.setName("male inhabitant");
        id1.setWeeklyPubVisits(0);
        FemaleHumanDomain id2 = new FemaleHumanDomain();
        id2.setName("female inhabitant");
        id2.setHairColourCount(1);
        List<AbstractHumanDomain> inhabitants = new ArrayList<>();
        inhabitants.add(id1);
        inhabitants.add(id2);
        hd.setOwner(owner);
        hd.setInhabitants(inhabitants);

        HouseTo ht = jTransfo.convert(hd, new HouseTo());

        assertThat(ht.getOwner()).isNotNull();
        assertThat(ht.getOwner()).isInstanceOf(MaleHumanTo.class);
        assertThat(ht.getOwner().getName()).isEqualTo("the owner");
        assertThat(((MaleHumanTo) ht.getOwner()).getWeeklyPubVisits()).isEqualTo(4);

        assertThat(ht.getInhabitants()).hasSize(2).
                extracting("name").contains("male inhabitant", "female inhabitant");
        assertThat(ht.getInhabitants().get(0)).isInstanceOf(MaleHumanTo.class);
        assertThat(((MaleHumanTo) ht.getInhabitants().get(0)).getWeeklyPubVisits()).isEqualTo(0);
        assertThat(ht.getInhabitants().get(1)).isInstanceOf(FemaleHumanTo.class);
        assertThat(((FemaleHumanTo) ht.getInhabitants().get(1)).getHairColourCount()).isEqualTo(1);
    }

    @Test
    public void testConvertTransferObject() throws Exception {
        HouseTo ht = new HouseTo();
        MaleHumanTo owner = new MaleHumanTo();
        owner.setName("the owner");
        owner.setWeeklyPubVisits(4);
        MaleHumanTo it1 = new MaleHumanTo();
        it1.setName("male inhabitant");
        it1.setWeeklyPubVisits(0);
        FemaleHumanTo it2 = new FemaleHumanTo();
        it2.setName("female inhabitant");
        it2.setHairColourCount(1);
        List<AbstractHumanTo> inhabitants = new ArrayList<>();
        inhabitants.add(it1);
        inhabitants.add(it2);
        ht.setOwner(owner);
        ht.setInhabitants(inhabitants);

        HouseDomain hd = jTransfo.convert(ht, new HouseDomain());

        assertThat(hd.getOwner()).isNotNull();
        assertThat(hd.getOwner()).isInstanceOf(MaleHumanDomain.class);
        assertThat(hd.getOwner().getName()).isEqualTo("the owner");
        assertThat(((MaleHumanDomain) hd.getOwner()).getWeeklyPubVisits()).isEqualTo(4);

        assertThat(hd.getInhabitants()).hasSize(2).
                extracting("name").contains("male inhabitant", "female inhabitant");
        assertThat(hd.getInhabitants().get(0)).isInstanceOf(MaleHumanDomain.class);
        assertThat(((MaleHumanDomain) hd.getInhabitants().get(0)).getWeeklyPubVisits()).isEqualTo(0);
        assertThat(hd.getInhabitants().get(1)).isInstanceOf(FemaleHumanDomain.class);
        assertThat(((FemaleHumanDomain) hd.getInhabitants().get(1)).getHairColourCount()).isEqualTo(1);
    }
}

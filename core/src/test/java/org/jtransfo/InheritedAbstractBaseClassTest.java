/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.object.AbstractHumanDomain;
import org.jtransfo.object.AbstractHumanTo;
import org.jtransfo.object.FemaleHumanDomain;
import org.jtransfo.object.FemaleHumanTo;
import org.jtransfo.object.GroupDomain;
import org.jtransfo.object.GroupTo;
import org.jtransfo.object.MaleHumanDomain;
import org.jtransfo.object.MaleHumanTo;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for using jTransfo when the transfer objects have an abstract base class and inheritance.
 */
public class InheritedAbstractBaseClassTest {

    private JTransfo jTransfo;

    @Before
    public void setup() throws Exception {
        ConfigurableJTransfo jTransfoImpl = JTransfoFactory.get();
        jTransfo = jTransfoImpl;

        ListTypeConverter ltc = new ListTypeConverter("abstractHumanToList", AbstractHumanTo.class);
        jTransfoImpl.getTypeConverters().add(ltc);
        jTransfoImpl.updateTypeConverters();
    }

    @Test
    public void testConvertDomainObject() throws Exception {
        GroupDomain gd = new GroupDomain();
        FemaleHumanDomain ld = new FemaleHumanDomain();
        ld.setName("leader");
        ld.setHairColourCount(3);
        MaleHumanDomain sd1 = new MaleHumanDomain();
        sd1.setName("male slave");
        sd1.setWeeklyPubVisits(0);
        FemaleHumanDomain sd2 = new FemaleHumanDomain();
        sd2.setName("female slave");
        sd2.setHairColourCount(1);
        List<AbstractHumanDomain> slaves = new ArrayList<>();
        slaves.add(sd1);
        slaves.add(sd2);
        gd.setLeader(ld);
        gd.setSlaves(slaves);

        GroupTo gt = jTransfo.convert(gd, new GroupTo());

        assertThat(gt.getLeader()).isNotNull();
        assertThat(gt.getLeader().getName()).isEqualTo("leader");
        assertThat(gt.getLeader()).isInstanceOf(FemaleHumanTo.class);
        assertThat(((FemaleHumanTo) gt.getLeader()).getHairColourCount()).isEqualTo(3);
        
        assertThat(gt.getSlaves()).hasSize(2).
                extracting("name").contains("male slave", "female slave");
        assertThat(gt.getSlaves().get(0)).isInstanceOf(MaleHumanTo.class);
        assertThat(((MaleHumanTo) gt.getSlaves().get(0)).getWeeklyPubVisits()).isEqualTo(0);
        assertThat(gt.getSlaves().get(1)).isInstanceOf(FemaleHumanTo.class);
        assertThat(((FemaleHumanTo) gt.getSlaves().get(1)).getHairColourCount()).isEqualTo(1);
    }

    @Test
    public void testConvertTransferObject() throws Exception {
        GroupTo gt = new GroupTo();
        FemaleHumanTo lt = new FemaleHumanTo();
        lt.setName("leader");
        lt.setHairColourCount(3);
        MaleHumanTo st1 = new MaleHumanTo();
        st1.setName("male slave");
        st1.setWeeklyPubVisits(0);
        FemaleHumanTo st2 = new FemaleHumanTo();
        st2.setName("female slave");
        st2.setHairColourCount(1);
        List<AbstractHumanTo> slaves = new ArrayList<>();
        slaves.add(st1);
        slaves.add(st2);
        gt.setLeader(lt);
        gt.setSlaves(slaves);

        GroupDomain gd = jTransfo.convert(gt, new GroupDomain());

        assertThat(gd.getLeader()).isNotNull();
        assertThat(gd.getLeader().getName()).isEqualTo("leader");
        assertThat(gd.getLeader()).isInstanceOf(FemaleHumanDomain.class);
        assertThat(((FemaleHumanDomain) gd.getLeader()).getHairColourCount()).isEqualTo(3);

        assertThat(gd.getSlaves()).hasSize(2).
                extracting("name").contains("male slave", "female slave");
        assertThat(gd.getSlaves().get(0)).isInstanceOf(MaleHumanDomain.class);
        assertThat(((MaleHumanDomain) gd.getSlaves().get(0)).getWeeklyPubVisits()).isEqualTo(0);
        assertThat(gd.getSlaves().get(1)).isInstanceOf(FemaleHumanDomain.class);
        assertThat(((FemaleHumanDomain) gd.getSlaves().get(1)).getHairColourCount()).isEqualTo(1);
    }

}

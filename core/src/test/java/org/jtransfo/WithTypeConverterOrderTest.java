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
import org.jtransfo.object.HouseDomain;
import org.jtransfo.object.HouseTo;
import org.jtransfo.object.MaleHumanDomain;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test to verify that the default type converters can be overridden.
 */
public class WithTypeConverterOrderTest {


    @Test
    public void testHasListConverterCanOverride() {
        ConfigurableJTransfo jTransfo = JTransfoFactory.get();

        HouseDomain houseDomain = setupHouseDomain();

        HouseTo htSortedInhabitants = jTransfo.convert(houseDomain, new HouseTo());

        assertThat(htSortedInhabitants.getInhabitants()).isNotEmpty();
        List<AbstractHumanTo> copySorted = new ArrayList<>(htSortedInhabitants.getInhabitants());
        Collections.sort(copySorted);
        assertThat(htSortedInhabitants.getInhabitants()).isEqualTo(copySorted); // verify sorted

        jTransfo.with(new AutomaticListTypeConverter()); // not sorted

        HouseTo htUnsortedInhabitants = jTransfo.convert(houseDomain, new HouseTo());

        assertThat(htUnsortedInhabitants.getInhabitants()).isNotEmpty();
        assertThat(htUnsortedInhabitants.getInhabitants()).isNotEqualTo(copySorted); // verify not sorted
    }

    private HouseDomain setupHouseDomain() {
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
        return hd;
    }

}

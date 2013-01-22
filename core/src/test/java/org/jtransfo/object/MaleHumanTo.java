/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Male human.
 */
@DomainClass(domainClass = MaleHumanDomain.class)
public class MaleHumanTo extends AbstractHumanTo {

    private int weeklyPubVisits;

    public int getWeeklyPubVisits() {
        return weeklyPubVisits;
    }

    public void setWeeklyPubVisits(int weeklyPubVisits) {
        this.weeklyPubVisits = weeklyPubVisits;
    }
}

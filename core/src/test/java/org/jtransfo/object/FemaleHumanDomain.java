/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

/**
 * Female human.
 */
public class FemaleHumanDomain extends AbstractHumanDomain {

    private int hairColourCount;

    public int getHairColourCount() {
        return hairColourCount;
    }

    public void setHairColourCount(int hairColourCount) {
        this.hairColourCount = hairColourCount;
    }

}

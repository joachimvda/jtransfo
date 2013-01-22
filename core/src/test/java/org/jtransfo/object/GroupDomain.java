/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import java.util.List;

/**
 * Group of humans.
 */
public class GroupDomain {

    private AbstractHumanDomain leader;
    private List<AbstractHumanDomain> slaves;

    public AbstractHumanDomain getLeader() {
        return leader;
    }

    public void setLeader(AbstractHumanDomain leader) {
        this.leader = leader;
    }

    public List<AbstractHumanDomain> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<AbstractHumanDomain> slaves) {
        this.slaves = slaves;
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;

import java.util.List;

/**
 * Group of humans with leader..
 */
@DomainClass(domainClass = GroupDomain.class)
public class GroupTo {

    private AbstractHumanTo leader;
    @MappedBy(typeConverter = "abstractHumanToList")
    private List<AbstractHumanTo> slaves;

    public AbstractHumanTo getLeader() {
        return leader;
    }

    public void setLeader(AbstractHumanTo leader) {
        this.leader = leader;
    }

    public List<AbstractHumanTo> getSlaves() {
        return slaves;
    }

    public void setSlaves(List<AbstractHumanTo> slaves) {
        this.slaves = slaves;
    }
}

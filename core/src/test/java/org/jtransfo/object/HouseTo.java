package org.jtransfo.object;

import java.util.List;

import org.jtransfo.DomainClass;

/**
 * House with owner and inhabitants.
 */
@DomainClass(domainClass = HouseDomain.class)
public class HouseTo {

    private AbstractHumanTo owner;
    private List<AbstractHumanTo> inhabitants;

    public AbstractHumanTo getOwner() {
        return owner;
    }

    public void setOwner(AbstractHumanTo owner) {
        this.owner = owner;
    }

    public List<AbstractHumanTo> getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(List<AbstractHumanTo> inhabitants) {
        this.inhabitants = inhabitants;
    }
}

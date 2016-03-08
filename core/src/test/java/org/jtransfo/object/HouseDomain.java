package org.jtransfo.object;

import java.util.List;

/**
 * House with owner and inhabitants.
 */
public class HouseDomain {

    private AbstractHumanDomain owner;
    private List<AbstractHumanDomain> inhabitants;

    public AbstractHumanDomain getOwner() {
        return owner;
    }

    public void setOwner(AbstractHumanDomain owner) {
        this.owner = owner;
    }

    public List<AbstractHumanDomain> getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(List<AbstractHumanDomain> inhabitants) {
        this.inhabitants = inhabitants;
    }
}

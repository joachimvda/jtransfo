package org.jtransfo.object;

import java.util.List;

/**
 * House with owner and inhabitants.
 */
public class HouseDomain {

    private AbstractHumanDomain owner;
    private List<AbstractHumanDomain> inhabitants;
    private List<String> petNames;

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

    public List<String> getPetNames() {
        return petNames;
    }

    public void setPetNames(List<String> petNames) {
        this.petNames = petNames;
    }

}

package org.jtransfo.object;

import java.util.Set;

/**
 * House with owner and inhabitants.
 */
public class FlatDomain {

    private AbstractHumanDomain owner;
    private Set<AbstractHumanDomain> inhabitants;
    private Set<String> petNames;

    public AbstractHumanDomain getOwner() {
        return owner;
    }

    public void setOwner(AbstractHumanDomain owner) {
        this.owner = owner;
    }

    public Set<AbstractHumanDomain> getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(Set<AbstractHumanDomain> inhabitants) {
        this.inhabitants = inhabitants;
    }

    public Set<String> getPetNames() {
        return petNames;
    }

    public void setPetNames(Set<String> petNames) {
        this.petNames = petNames;
    }

}

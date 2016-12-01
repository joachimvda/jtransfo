package org.jtransfo.object;

import org.jtransfo.DomainClass;

import java.util.Set;

/**
 * House with owner and inhabitants.
 */
@DomainClass(domainClass = FlatDomain.class)
public class FlatTo {

    private AbstractHumanTo owner;
    private Set<AbstractHumanTo> inhabitants;
    private Set<String> petNames;

    public AbstractHumanTo getOwner() {
        return owner;
    }

    public void setOwner(AbstractHumanTo owner) {
        this.owner = owner;
    }

    public Set<AbstractHumanTo> getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(Set<AbstractHumanTo> inhabitants) {
        this.inhabitants = inhabitants;
    }

    public Set<String> getPetNames() {
        return petNames;
    }

    public void setPetNames(Set<String> petNames) {
        this.petNames = petNames;
    }

}

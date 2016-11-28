package org.jtransfo.object;

import org.jtransfo.DomainClass;

import java.util.List;

/**
 * House with owner and inhabitants.
 */
@DomainClass(domainClass = HouseDomain.class)
public class HouseTo {

    private AbstractHumanTo owner;
    private List<AbstractHumanTo> inhabitants;
    private List<String> petNames;

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

    public List<String> getPetNames() {
        return petNames;
    }

    public void setPetNames(List<String> petNames) {
        this.petNames = petNames;
    }

}

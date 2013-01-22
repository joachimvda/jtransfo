package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.DomainClassDelegate;

/**
 * Base human.
 */
@DomainClass(domainClass = AbstractHumanDomain.class)
@DomainClassDelegate(delegates = {MaleHumanTo.class, FemaleHumanTo.class})
public class AbstractHumanTo {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

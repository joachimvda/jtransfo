package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.DomainClassDelegate;

/**
 * Base human.
 */
@DomainClass(domainClass = AbstractHumanDomain.class)
@DomainClassDelegate(delegates = {MaleHumanTo.class, FemaleHumanTo.class})
public class AbstractHumanTo implements Comparable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    protected void setNameAlternate(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof AbstractHumanTo)) {
            return -1;
        }
        return name.compareTo(((AbstractHumanTo) o).name);
    }

}

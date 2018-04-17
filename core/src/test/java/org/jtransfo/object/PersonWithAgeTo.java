package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Person with age to.
 */
@DomainClass(domainClass = PersonWithAgeDomain.class)
public class PersonWithAgeTo extends PersonTo {

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

package org.jtransfo.object;

/**
 * Person with age.
 */
public class PersonWithAgeDomainImpl extends PersonDomain implements PersonWithAgeDomain {

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

}

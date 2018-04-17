package org.jtransfo.object;

import java.util.Date;

/**
 * Interface voor PersonWithAgeDomainImpl.
 */
public interface PersonWithAgeDomain {

    String getName();
    void setName(String name);
    Gender getGender();
    void setGender(Gender gender);
    AddressDomain getAddress();
    void setAddress(AddressDomain address);
    Date getLastChanged();
    void setLastChanged(Date lastChanged);
    int getAge();
    void setAge(int age);

}

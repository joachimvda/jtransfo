package org.jtransfo.spring;

import org.jtransfo.ObjectFinder;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;

/**
 * Address finder which always looks in a foxed list.
 */
public class AddressFinder implements ObjectFinder {

    private AddressDomain[] addresses;

    public AddressFinder() {
        addresses = new AddressDomain[10];
        for (int i = 0 ; i < 10 ; i++) {
            AddressDomain ad = new AddressDomain();
            ad.setId(Long.valueOf(i));
            ad.setAddress("Address " + i);
            addresses[i] = ad;
        }
    }

    public <T> T getObject(Class<T> domainClass, Object to) {
        if (domainClass.isAssignableFrom(AddressDomain.class) && to instanceof AddressTo) {
            return (T) addresses[((AddressTo) to).getId().intValue()];
        }
        return null;
    }

}

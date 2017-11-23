package org.jtransfo.object;

import org.jtransfo.PostConverter;

/**
 * Double the name post-conversion.
 */
public class DoubleNamePostConverter implements PostConverter<PersonTo, PersonDomain> {

    @Override
    public void postConvertToTo(PersonDomain source, PersonTo target, String... tags) {
        target.setName(target.getName() + target.getName());
    }

    @Override
    public void postConvertToDomain(PersonTo source, PersonDomain target, String... tags) {
        target.setName(target.getName() + target.getName());
    }
}

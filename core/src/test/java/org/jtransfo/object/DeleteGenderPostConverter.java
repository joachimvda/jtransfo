package org.jtransfo.object;

import org.jtransfo.Named;
import org.jtransfo.PostConverter;

/**
 * Delete the Gender.
 */
public class DeleteGenderPostConverter implements PostConverter<PersonTo, PersonDomain>, Named {

    @Override
    public String getName() {
        return "deleteGender";
    }

    @Override
    public void postConvertToTo(PersonDomain source, PersonTo target, String... tags) {
        target.setGender(null);
    }

    @Override
    public void postConvertToDomain(PersonTo source, PersonDomain target, String... tags) {
        target.setGender(null);
    }
}

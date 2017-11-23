package org.jtransfo.object;

import org.jtransfo.PreConverter;

/**
 * Skip conversion when name=="Me".
 */
public class SkipMePreConverter implements PreConverter<PersonTo, PersonDomain> {

    private final String name;

    /** Just skip "Me". */
    public SkipMePreConverter() {
        name = "Me";
    }

    /**
     * Skip for given name.
     *
     * @param name name to skip
     */
    SkipMePreConverter(String name) {
        this.name = name;
    }

    @Override
    public Result preConvertToTo(PersonDomain source, PersonTo target, String... tags) {
        if (name.equalsIgnoreCase(source.getName())) {
            return Result.SKIP;
        }
        return Result.CONTINUE;
    }

    @Override
    public Result preConvertToDomain(PersonTo source, PersonDomain target, String... tags) {
        if (name.equalsIgnoreCase(source.getName())) {
            return Result.SKIP;
        }
        return Result.CONTINUE;
    }
}

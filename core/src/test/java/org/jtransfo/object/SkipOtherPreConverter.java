package org.jtransfo.object;

import org.jtransfo.Named;

/**
 * Skip conversion when name=="Other".
 */
public class SkipOtherPreConverter extends SkipMePreConverter implements Named {

    @Override
    public String getName() {
        return "skipOther";
    }

    public SkipOtherPreConverter() {
        super("Other");
    }
}

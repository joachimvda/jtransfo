package org.jtransfo.object;

/**
 * Interface with default method to check correct reading of inherited default fields.
 */
public interface ExtraGetterOnFieldAlternative {

    String getFieldAlternate();

    default String getFieldDouble() {
        return getFieldAlternate() + getFieldAlternate();
    }

}

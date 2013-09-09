package org.jtransfo.object;

/**
 * Domain object with various boolean fields.
 */
public class BooleanDomain {

    private boolean boolean1;
    private boolean isBoolean2;
    private boolean hasBoolean3;
    private boolean boolean4;
    private boolean zzzBoolean6;

    public boolean isBoolean1() {
        return boolean1;
    }

    public void setBoolean1(boolean boolean1) {
        this.boolean1 = boolean1;
    }

    public boolean isBoolean2() {
        return isBoolean2;
    }

    public void setBoolean2(boolean boolean2) {
        isBoolean2 = boolean2;
    }

    public boolean isHasBoolean3() {
        return hasBoolean3;
    }

    public void setHasBoolean3(boolean hasBoolean3) {
        this.hasBoolean3 = hasBoolean3;
    }

    public boolean hasBoolean4() {
        return boolean4;
    }

    public void setBoolean4(boolean boolean4) {
        this.boolean4 = boolean4;
    }

    public boolean isBoolean5() {
        return boolean1 && boolean4;
    }

    public boolean isIsBoolean6() {
        return zzzBoolean6;
    }

    public void setIsBoolean6(boolean boolean6) {
        zzzBoolean6 = boolean6;
    }
}

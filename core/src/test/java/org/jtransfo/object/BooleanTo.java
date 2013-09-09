package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;

/**
 * Transfer object with various boolean fields.
 */
@DomainClass(domainClass = BooleanDomain.class)
public class BooleanTo {

    private boolean boolean1;
    private boolean isBoolean2;
    private boolean hasBoolean3;
    private boolean boolean4;
    @MappedBy(readOnly = true)
    private boolean boolean5;
    private boolean isBoolean6;

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

    public boolean isBoolean4() {
        return boolean4;
    }

    public void setBoolean4(boolean boolean4) {
        this.boolean4 = boolean4;
    }

    public boolean isBoolean5() {
        return boolean5;
    }

    public void setBoolean5(boolean boolean5) {
        this.boolean5 = boolean5;
    }

    public boolean isBoolean6() {
        return isBoolean6;
    }

    public void setBoolean6(boolean boolean6) {
        isBoolean6 = boolean6;
    }
}

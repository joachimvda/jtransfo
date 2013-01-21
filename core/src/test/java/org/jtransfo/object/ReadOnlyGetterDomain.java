package org.jtransfo.object;

/**
 * Domain object with a getter which needs to be put in transfer object.
 */
public class ReadOnlyGetterDomain {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTwice() {
        if (null == id) {
            return null;
        }
        return id + id;
    }
}

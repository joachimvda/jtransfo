package org.jtransfo.object;

public enum SomeEnum {

    SOME("something"),
    OTHER("or other");

    private String description;

    SomeEnum(String description) {
        this.description = description;
    }
}

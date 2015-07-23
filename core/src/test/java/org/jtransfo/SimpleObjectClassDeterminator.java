package org.jtransfo;

/**
 * Fallback implementation of {@link ObjectClassDeterminator} which is used to have a default value.
 */
public class SimpleObjectClassDeterminator implements ObjectClassDeterminator {

    @Override
    public Class getObjectClass(Object object) {
        return object.getClass();
    }

}

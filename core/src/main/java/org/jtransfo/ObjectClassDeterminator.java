/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Implement and add object class determinators to allow jTransfo to determine the correct class for an object,
 * even if it is a proxy.
 */
public interface ObjectClassDeterminator {

    /**
     * Get the specific class for an object.
     *
     * You should only catch special cases and return null if the special case does not apply.
     * If no special case applied, jTransfo will use the objects class anyway.
     *
     * @param object object fot which the class needs to be determined
     * @return class for the object or null
     */
    Class getObjectClass(Object object);

}

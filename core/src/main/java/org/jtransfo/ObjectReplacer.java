/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Allow objects to be replaced before conversion. This can be useful to change proxies into real objects.
 */
public interface ObjectReplacer {

    /**
     * Convert an object to something jTransfo should convert.
     *
     * If the object is not replaced, the object itself should be returned.
     *
     * @param object object which may need to be replaced
     * @return replacement object or the object itself
     */
    Object replaceObject(Object object);

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Interface which allows obtaining an object name.
 */
public interface Named {

    /**
     * Get name for the object.
     *
     * @return object name
     */
    String getName();
}

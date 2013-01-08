/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Marked which can be applied on {@link TypeConverter} implementations to force injecting the jTransfo instance.
 */
public interface NeedsJTransfo {

    /**
     * Set jTransfo instance which can be used recursively.
     *
     * @param jTransfo jTransfo instance
     */
    void setJTransfo(JTransfo jTransfo);
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.JTransfoImpl;

/**
 * Factory to get a {@link JTransfo} instance.
 */
public final class JTransfoFactory {

    private JTransfoFactory() {
        throw new IllegalAccessError("Utility class, do not construct.");
    }

    /**
     * Get a {@link ConfigurableJTransfo} instance which can be configured further.
     *
     * @return {@link JTransfo} instance
     */
    public static ConfigurableJTransfo get() {
        return new JTransfoImpl();
    }

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

/**
 * Interface which indicates how to get the database id from the transfer object.
 */
public interface IdentifiedTo {

    /**
     * Get the database id.
     *
     * @return database id
     */
    Long getId();
}

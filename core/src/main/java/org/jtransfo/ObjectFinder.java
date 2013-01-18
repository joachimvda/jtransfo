/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Allow domain objects to be discovered from the transfer object.
 * <p/>
 * This is typically used to get the base object from the database.
 */
public interface ObjectFinder {

    /**
     * Try to get the domain object for the given transfer object.
     * <p/>
     * Return null when no domain object found or when it is unknown how to create the object.
     *
     * @param <T> domain class type
     * @param domainClass type of class needed
     * @param to transfer object which needs to be converted
     * @return base domain object or null if object finder cannot handle this case
     * @throws JTransfoException something went wrong while trying to find object
     */
    <T> T getObject(Class<T> domainClass, Object to) throws JTransfoException;
}

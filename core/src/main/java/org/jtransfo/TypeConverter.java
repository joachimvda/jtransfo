/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Do type conversion to allow object to be stored using a different type in transfer and domain objects.
 *
 * @param <TO_TYPE> type in transfer object
 * @param <DOMAIN_TYPE> type in domain object
 */
public interface TypeConverter<TO_TYPE, DOMAIN_TYPE> {

    /**
     * Convert a transfer object field value to the value for the domain object.
     *
     * @param object object to convert
     * @return converted object
     * @throws JTransfoException problem during type conversion
     */
    DOMAIN_TYPE convert(TO_TYPE object) throws JTransfoException;

    /**
     * Convert a domain object field value to the value for the transfer object.
     *
     * @param object object to convert
     * @return converted object
     * @throws JTransfoException problem during type conversion
     */
    TO_TYPE reverse(DOMAIN_TYPE object) throws JTransfoException;

}

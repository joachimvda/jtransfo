/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.lang.reflect.Type;

import org.jtransfo.internal.SyntheticField;

/**
 * Do type conversion to allow object to be stored using a different type in transfer and domain objects.
 *
 * @param <TO_TYPE> type in transfer object
 * @param <DOMAIN_TYPE> type in domain object
 */
public interface TypeConverter<TO_TYPE, DOMAIN_TYPE> {

    /**
     * Can this type converter handle conversions between the given transfer and domain object types?
     *
     * @param realToType real class for the type in the transfer object
     * @param realDomainType real class for the type in the domain object
     * @return true is this type converter can handle the type conversions
     */
    boolean canConvert(Type realToType, Type realDomainType);

    /**
     * Convert a transfer object field value to the value for the domain object.
     *
     * @param object object to convert
     * @param domainField target domain field
     * @param domainObject target domain object
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return converted object
     * @throws JTransfoException problem during type conversion
     */
    DOMAIN_TYPE convert(TO_TYPE object, SyntheticField domainField, Object domainObject, String... tags)
            throws JTransfoException;

    /**
     * Convert a domain object field value to the value for the transfer object.
     *
     * @param object object to convert
     * @param toField target transfer object field
     * @param toObject target transfer object
     * @param tags tags which indicate which fields can be converted based on {@link MapOnly} annotations.
     * @return converted object
     * @throws JTransfoException problem during type conversion
     */
    TO_TYPE reverse(DOMAIN_TYPE object, SyntheticField toField, Object toObject, String... tags)
            throws JTransfoException;

}

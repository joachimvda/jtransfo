/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.LockableList;

import java.util.List;

/**
 * Set of converters for a TO.
 * <p>
 * Note that this class is not thread safe!
 * </p>
 */
public class ToConverter {

    private final LockableList<Converter> toTo = new LockableList<>();
    private final LockableList<Converter> toDomain = new LockableList<>();
    private final PreConverter preConverter;

    /**
     * Constructor without preconverter.
     */
    public ToConverter() {
        this.preConverter = new PreConverter() { };
    }

    /**
     * Constructor with a given preconverter.
     *
     * @param preConverter preconverter to use
     */
    public ToConverter(PreConverter preConverter) {
        this.preConverter = preConverter;
    }

    /**
     * Get list of converters to convert domain to transfer object.
     *
     * @return list of converters
     */
    public List<Converter> getToTo() {
        return toTo;
    }

    /**
     * Add a converters to convert domain to transfer object.
     *
     * @param converter converter to add
     * @return list of converters
     */
    public List<Converter> addToTo(Converter converter) {
        toTo.add(converter);
        return toTo;
    }

    /**
     * Get list of converters to convert transfer to domain object.
     *
     * @return list of converters
     */
    public List<Converter> getToDomain() {
        return toDomain;
    }

    /**
     * Add a converter to convert transfer to domain object.
     *
     * @param converter converter to add
     * @return list of converters
     */
    public List<Converter> addToDomain(Converter converter) {
        toDomain.add(converter);
        return toDomain;
    }

    /**
     * Assure that the object can not be modified any more.
     */
    public void lock() {
        toTo.lock();
        toDomain.lock();
    }

    /**
     * Get the {@link PreConverter} to use.
     *
     * @return pre converter
     */
    public PreConverter getPreConverter() {
        return preConverter;
    }

}

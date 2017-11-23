/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.PreConverter;

import java.util.List;

/**
 * Build a {@link PreConverter} which combines several preconverters.
 *
 * @param <T> transfer object type
 * @param <D> domain object type
 */
class CombinedPreConverter<T, D> implements PreConverter<T, D> {

    private final List<PreConverter<T, D>> preConverters;

    /**
     * Construct a CombinedPreConverter for a list of preconverters.
     *
     * @param preConverters list to combine
     */
    CombinedPreConverter(List<PreConverter<T, D>> preConverters) {
        this.preConverters = preConverters;
    }

    @Override
    public Result preConvertToTo(D source, T target, String... tags) {
        for (PreConverter<T, D> converter : preConverters) {
            Result result = converter.preConvertToTo(source, target, tags);
            if (PreConverter.Result.SKIP == result) {
                return PreConverter.Result.SKIP;
            }
        }
        return PreConverter.Result.CONTINUE;
    }

    @Override
    public Result preConvertToDomain(T source, D target, String... tags) {
        for (PreConverter<T, D> converter : preConverters) {
            Result result = converter.preConvertToDomain(source, target, tags);
            if (PreConverter.Result.SKIP == result) {
                return PreConverter.Result.SKIP;
            }
        }
        return PreConverter.Result.CONTINUE;
    }
    
}

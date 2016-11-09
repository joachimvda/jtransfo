/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.Converter;
import org.jtransfo.JTransfoException;
import org.jtransfo.MapOnly;

import java.util.HashMap;
import java.util.Map;

/**
 * Converter which delegated to the specified converters for the tags. It will always also handle "*" as first tag.
 */
public class TaggedConverter implements Converter {

    private Map<String, Converter> converters = new HashMap<>();

    /**
     * Add the converter which should be used for a specific tag.
     *
     * @param tags tags for which the converter applies
     * @param converter converter for the tag
     */
    public void addConverters(String[] tags, Converter converter) {
        for (String tag : tags) {
            converters.put(tag, converter);
        }
    }

    @Override
    public void convert(Object source, Object target, String... tags) throws JTransfoException {
        Converter converter = converters.get(MapOnly.ALWAYS);
        if (null != converter) {
            converter.convert(source, target, tags);
        }
        if (null != tags) {
            for (String tag : tags) {
                converter = converters.get(tag);
                if (null != converter) {
                    converter.convert(source, target, tags);
                }
            }
        }
    }
}

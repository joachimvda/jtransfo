/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with tags.
 */
public final class TagsUtil {

    private TagsUtil() {
        throw new IllegalAccessError("TagsUtil cannot be instantiated.");
    }

    /**
     * Add extra tags to the existing set.
     *
     * @param base base set of tags
     * @param extraTags tags which need to be added
     * @return class
     */
    public static String[] add(String[] base, String... extraTags) {
        if (null == base || base.length == 0) {
            return extraTags;
        }
        if (extraTags.length == 0) {
            return base;
        }
        List<String> tags = new ArrayList<>(Arrays.asList(base));
        tags.addAll(Arrays.asList(extraTags));
        return tags.toArray(new String[tags.size()]);
    }

}

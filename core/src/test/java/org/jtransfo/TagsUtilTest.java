/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test for TagsUtil.
 */
public class TagsUtilTest {

    private static final String[] TEST_TAGS_BLA = {"bla"};
    private static final String[] TEST_TAGS_ALB = {"alb"};
    private static final String[] TEST_TAGS_BLA_ALB = {"bla", "alb"};

    @Test
    public void add() {
        assertThat(TagsUtil.add(null)).isEqualTo(new String[0]);
        assertThat(TagsUtil.add(null, "alb")).isEqualTo(TEST_TAGS_ALB);
        assertThat(TagsUtil.add(TEST_TAGS_BLA)).isEqualTo(TEST_TAGS_BLA);
        assertThat(TagsUtil.add(TEST_TAGS_BLA, "alb")).isEqualTo(TEST_TAGS_BLA_ALB);
    }
    
}
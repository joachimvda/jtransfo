/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for {@link LockableList}.
 */
public class LockableListTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private LockableList<Integer> list;

    @Before
    public void setup() {
        list = new LockableList<Integer>();
        list.add(0);
        list.add(1);
        list.add(2);
    }

    @Test
    public void setTest() throws Exception {
        list.set(0, 3);
        assertThat(list.get(0)).isEqualTo(3);
        assertThat(list).hasSize(3);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.set(0, 0);
    }

    @Test
    public void addTest() throws Exception {
        list.add(3);
        assertThat(list).hasSize(4);
        assertThat(list.get(3)).isEqualTo(3);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.add(4);
    }

    @Test
    public void addPositionTest() throws Exception {
        list.add(0, 3);
        assertThat(list.get(0)).isEqualTo(3);
        assertThat(list).hasSize(4);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.add(0, 0);
    }

    @Test
    public void removeTest() throws Exception {
        list.remove(0);
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo(1);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.remove(0);
    }

    @Test
    public void removeObjectTest() throws Exception {
        list.remove(Integer.valueOf(0));
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo(1);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.remove(Integer.valueOf(1));
    }

    @Test
    public void clearTest() throws Exception {
        list.clear();
        assertThat(list).hasSize(0).isEmpty();
    }

    @Test
    public void clearLockedTest() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.clear();
    }

    @Test
    public void addAllTest() throws Exception {
        list.addAll(Collections.singletonList(3));
        assertThat(list).hasSize(4);
        assertThat(list.get(3)).isEqualTo(3);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.addAll(Collections.singletonList(4));
    }

    @Test
    public void addAllPositionTest() throws Exception {
        list.addAll(0, Collections.singletonList(3));
        assertThat(list).hasSize(4);
        assertThat(list.get(0)).isEqualTo(3);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.addAll(0, Collections.singletonList(4));
    }

    @Test
    public void removeRangeTest() throws Exception {
        list.removeRange(0, 1);
        assertThat(list).hasSize(2);
        assertThat(list.get(0)).isEqualTo(1);

        exception.expect(JTransfoException.class);
        exception.expectMessage("Collection is read-only.");
        list.lock();
        list.removeRange(0, 1);
    }


}

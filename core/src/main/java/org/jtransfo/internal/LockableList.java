/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list which can be locked to prevent further modifications.
 *
 * @param <T> list entry type
 */
public class LockableList<T> extends ArrayList<T> {

    private static final String READ_ONLY = "Collection is read-only.";

    private boolean readOnly;

    /**
     * Set read-only status for list. Once this is called, modifications to the list are no longer allowed.
     */
    public void lock() {
        readOnly = true;
    }

    @Override
    public T set(int i, T t) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.set(i, t);
    }

    @Override
    public boolean add(T t) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.add(t);
    }

    @Override
    public void add(int i, T t) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        super.add(i, t);
    }

    @Override
    public T remove(int i) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.remove(i);
    }

    @Override
    public boolean remove(Object o) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.remove(o);
    }

    @Override
    public void clear() {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        super.clear();
    }

    @Override
    public boolean addAll(Collection<? extends T> ts) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.addAll(ts);
    }

    @Override
    public boolean addAll(int i, Collection<? extends T> ts) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        return super.addAll(i, ts);
    }

    @Override
    protected void removeRange(int i, int i1) {
        if (readOnly) {
            throw new JTransfoException(READ_ONLY);
        }
        super.removeRange(i, i1);
    }
}

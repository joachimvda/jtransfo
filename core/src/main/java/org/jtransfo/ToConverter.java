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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Set of converters for a TO.
 * <p/>
 * Note that this class is not thread safe!
 */
public class ToConverter {

    private final LockableList<Converter> toTo = new LockableList<Converter>();
    private final LockableList<Converter> toDomain = new LockableList<Converter>();

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
        toTo.setReadOnly();
        toDomain.setReadOnly();
    }

    private class LockableList<T> extends ArrayList<T> {

        private static final String READ_ONLY = "Collection is read-only.";

        private boolean readOnly;

        public void setReadOnly() {
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
            return super.addAll(ts);    // @todo implement
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
}

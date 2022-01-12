/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * Simple synthetic field which just exposes the field itself.
 */
public class SimpleSyntheticField implements SyntheticField {

    private Field field;

    /**
     * Constructor.
     *
     * @param field field (may be in a parent class of clazz)
     */
    public SimpleSyntheticField(Field field) {
        this.field = field;
    }

    @Override
    public Object get(Object object) throws IllegalAccessException, IllegalArgumentException {
        return field.get(object);
    }

    @Override
    public void set(Object object, Object value) throws IllegalAccessException, IllegalArgumentException {
        field.set(object, value);
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Optional<Field> getField() {
        return Optional.ofNullable(field);
    }
}

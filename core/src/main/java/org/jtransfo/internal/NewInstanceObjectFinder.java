/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoException;
import org.jtransfo.ObjectFinder;

import java.util.Arrays;

/**
 * Object finder which creates a new object using the no-arguments constructor.
 */
public class NewInstanceObjectFinder implements ObjectFinder {

    private ReflectionHelper reflectionHelper = new ReflectionHelper();

    @Override
    public <T> T getObject(Class<T> domainClass, Object to, String... tags) throws JTransfoException {
        try {
            if (Arrays.stream(tags).noneMatch(JTransfo.TAG_WHEN_READ_ONLY_DOMAIN::equals)) {
                return reflectionHelper.newInstance(domainClass);
            } else {
                return null;
            }
        } catch (InstantiationException | IllegalAccessException ie) {
            throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName() + ".", ie);
        }
    }
}

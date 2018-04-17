/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Allow classes to be replaced before being used for conversion. This can be useful to swap interfaces interfaces to
 * real objects in domain mappings. This is applied to the class specified in {@link DomainClass} annotations and to
 * the target class in {@link JTransfo#convertTo(Object, Class, String...)}.
 */
public interface ClassReplacer {

    /**
     * Convert a class to a different class. To make sure this can work, classes should only be converted to child
     * classes.
     *
     * If the class is not replaced, the class itself should be returned.
     *
     * @param clazz class which may need to be replaced
     * @return replacement class or the class itself
     */
    Class replaceClass(Class clazz);

}

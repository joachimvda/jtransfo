/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.cdi.domain;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.jtransfo.cdi.InterceptorOrder;

@InterceptorOrder(1) // should be a lower order than SetExtraConvertInterceptor to assure it being applied "after"
public class AppendExtraConvertInterceptor implements ConvertInterceptor {

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
        T res = next.convert(source, target, isTargetTo, tags);
        if (target instanceof PersonDomain) {
            PersonDomain person = (PersonDomain) res;
            person.setExtra(person.getExtra() + " sleep.");
        }
        return res;
    }

}

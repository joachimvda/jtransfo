/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.spring.domain;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Set base value in "extra" field in PersonDomain. Used to convert interceptors and order on convert interceptors.
 */
@Component
@Order(2) // should be at higher order than AppendExtraConvertInterceptor to assure it being applied "before"
public class SetExtraConvertInterceptor implements ConvertInterceptor {

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
        T res = next.convert(source, target, isTargetTo, tags);
        if (target instanceof PersonDomain) {
            PersonDomain person = (PersonDomain) res;
            person.setExtra("Extra");
        }
        return res;
    }

}

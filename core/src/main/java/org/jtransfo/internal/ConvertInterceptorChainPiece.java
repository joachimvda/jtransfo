/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;

/**
 * A piece of the chain with convert interceptors.
 */
public class ConvertInterceptorChainPiece implements ConvertSourceTarget {

    private ConvertSourceTarget next;
    private ConvertInterceptor interceptor;

    /**
     * Constructor.
     *
     * @param interceptor actual interceptor for this piece of the chain
     * @param next next piece of the chain
     */
    public ConvertInterceptorChainPiece(ConvertInterceptor interceptor, ConvertSourceTarget next) {
        this.interceptor = interceptor;
        this.next = next;
    }

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, String... tags) {
        return interceptor.convert(source, target, isTargetTo, next, tags);
    }
}

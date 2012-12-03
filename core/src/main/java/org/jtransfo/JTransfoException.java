/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

/**
 * Exception during jTransfo handling.
 */
public class JTransfoException extends RuntimeException {

    /**
     * Problem during processing with message.
     *
     * @param message message
     */
    public JTransfoException(String message) {
        super(message);
    }

    /**
     * Problem during processing with message and cause.
     *
     * @param message message
     * @param throwable cause
     */
    public JTransfoException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

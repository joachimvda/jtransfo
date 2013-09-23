/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.JTransfoException;

import java.io.IOException;

public class SimpleBaseDomain {

    private String a;
    private String b;

    public String getA() throws IOException, JTransfoException, IllegalStateException {
        return a;
    }

    public void setA(String a) throws IOException, JTransfoException, IllegalStateException {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }
}

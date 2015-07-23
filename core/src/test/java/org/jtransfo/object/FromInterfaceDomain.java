/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

public class FromInterfaceDomain implements FromInterfaceDomainInterface {

    private String a;
    private String b;

    @Override
    public String getA() {
        return a;
    }

    @Override
    public void setA(String a) {
        this.a = a;
    }

    @Override
    public String getB() {
        return b;
    }

    @Override
    public void setB(String b) {
        this.b = b;
    }
}

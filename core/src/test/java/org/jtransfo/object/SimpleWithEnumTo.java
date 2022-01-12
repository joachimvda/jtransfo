/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;
import org.jtransfo.NotMapped;

@DomainClass(domainClass = SimpleWithEnumDomain.class)
public class SimpleWithEnumTo extends SimpleBaseTo {

    @MappedBy(field = "b")
    private String string;

    @MappedBy(readOnly = true)
    private String c;
    private int i;

    private String some;
    @MappedBy(readOnly = true, path = "some", field = "description")
    private String someDescription;

    @NotMapped
    private int j;

    private transient int k;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public String getSome() {
        return some;
    }

    public void setSome(String some) {
        this.some = some;
    }

    public String getSomeDescription() {
        return someDescription;
    }

    public void setSomeDescription(String someDescription) {
        this.someDescription = someDescription;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }
}

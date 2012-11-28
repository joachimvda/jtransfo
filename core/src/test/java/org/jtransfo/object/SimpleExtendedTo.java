/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.object;

import org.jtransfo.DomainClass;
import org.jtransfo.MappedBy;
import org.jtransfo.NotMapped;

/**
 * TO with base class.
 */
@DomainClass(domainClass = SimpleExtendedDomain.class)
public class SimpleExtendedTo extends SimpleBaseTo {

    @MappedBy(field = "b")
    private String string;

    @MappedBy(readOnly = true)
    private String c;
    private String i;

    @NotMapped
    private String j;

    private transient String k;

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

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public String getJ() {
        return j;
    }

    public void setJ(String j) {
        this.j = j;
    }

    public String getK() {
        return k;
    }

    public void setK(String k) {
        this.k = k;
    }
}

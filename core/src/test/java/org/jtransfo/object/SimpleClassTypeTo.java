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

@DomainClass(domainClass = SimpleClassDomain.class)
public class SimpleClassTypeTo {

	private String bla;

	public String getBla() {
		return bla;
	}

	public void setBla(String bla) {
		this.bla = bla;
	}
}

package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Simple TO class which uses the type reference to the domain object.
 *
 * @author Joachim Van der Auwera
 */
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

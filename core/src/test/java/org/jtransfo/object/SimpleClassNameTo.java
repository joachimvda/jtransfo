package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Transfer object with domain object specified as class name.
 *
 * @author Joachim Van der Auwera
 */
@DomainClass("org.jtransfo.object.SimpleClassDomain")
public class SimpleClassNameTo {

	private String bla;

	public String getBla() {
		return bla;
	}

	public void setBla(String bla) {
		this.bla = bla;
	}
}

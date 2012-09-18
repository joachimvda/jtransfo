package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.JTransfoException;

/**
 * Helper for handling transfer objects.
 *
 * @author Joachim Van der Auwera
 */
public final class ToHelper {

	private ToHelper() {
		// static class, hide constructor
	}

	/**
	 * Get domain class for transfer object.
	 *
	 * @param to transfer object
	 * @return domain class as annotated on class
	 */
	public static Class<?> getDomainClass(Object to) {
		Class<?> toClass = to.getClass();
		DomainClass domainClass = toClass.getAnnotation(DomainClass.class);
		if (null == domainClass) {
			throw new JTransfoException("Transfer object of type " + toClass.getName() +
					" not annotated with DomainClass.");
		}
		if (DomainClass.DEFAULT_CLASS.class != domainClass.domainClass()) {
			return domainClass.domainClass();
		}
		if (DomainClass.DEFAULT_NAME.equals(domainClass.value())) {
			throw new JTransfoException("Transfer object of type " + toClass.getName() +
					" DomainClass annotation does not specify class.");
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		if (null == cl) {
			cl = ToHelper.class.getClassLoader();
		}
		try {
			return cl.loadClass(domainClass.value());
		} catch (ClassNotFoundException cnfe) {
			throw new JTransfoException("Transfer object of type " + toClass.getName() +
					" DomainClass " + domainClass.value() + " not found.", cnfe);
		}
	}

}

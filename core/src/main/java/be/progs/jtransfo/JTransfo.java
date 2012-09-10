/**
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package be.progs.jtransfo;

import be.progs.jtransfo.internal.ToHelper;

import java.lang.annotation.Annotation;

/**
 * Class for converting to and from target objects.
 *
 * @author Joachim Van der Auwera
 */
public class JTransfo {

	private JTransfo() {
		// static class, hide constructor
	}

	/**
	 * Fill the target object with the values from the source object.
	 * <p/>
	 * This will write all values from the transfer object, other fields are not touched.
	 *
	 * @param source source object
	 * @param target target object
	 */
	public static void convert(Object source, Object target) {
		// @todo
	}

	/**
	 * Create a new domain object from the source transfer object.
	 * <p/>
	 * This only works if the domain object has a no-arguments constructor.
	 *
	 * @param source source transfer object
	 * @return domain object
	 */
	public static Object convert(Object source) {
		Class domainClass = ToHelper.getDomainClass(source);
		try {
			// @todo allow creation to be pluggable, could need domain lookup based on a field in the source
			return domainClass.newInstance();
		} catch (InstantiationException ie) {
			throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName(), ie);
		} catch (IllegalAccessException ie) {
			throw new JTransfoException("Cannot create instance for domain class " + domainClass.getName(), ie);
		}
	}

}
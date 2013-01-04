/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.jodatime;

import org.joda.time.DateTime;
import org.jtransfo.JTransfoException;
import org.jtransfo.TypeConverter;

import java.util.Date;

/**
 * Converter between {@link Date} and {@link DateTime}.
 */
public class DateDateTimeConverter implements TypeConverter<Date, DateTime> {

    @Override
    public boolean canConvert(Class<?> realToType, Class<?> realDomainType) {
        return Date.class.isAssignableFrom(realToType) && DateTime.class.isAssignableFrom(realDomainType);
    }

    @Override
    public DateTime convert(Date object, Class<DateTime> domainType) throws JTransfoException {
        if (null == object) {
            return null;
        }
        return new DateTime(object.getTime());
    }

    @Override
    public Date reverse(DateTime object, Class<Date> toType) throws JTransfoException {
        if (null == object) {
            return null;
        }
        return object.toDate();
    }
}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.jodatime;

import org.joda.time.DateTime;
import org.jtransfo.TypeConverter;
import org.jtransfo.object.Gender;
import org.junit.Test;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for DateDateTimeConverter.
 */
public class DateDateTimeConverterTest {

    private TypeConverter<Date, DateTime> typeConverter = new DateDateTimeConverter();

    private DateTime dt1 = new DateTime(2012, 10, 15, 7, 18, 58);
    private DateTime dt2 = new DateTime(2012, 2, 29, 11, 45);
    private DateTime dt3 = new DateTime(1974, 3, 31, 17, 7, 15);

    @Test
    public void testCanConvert() throws Exception {
        assertThat(typeConverter.canConvert(Date.class, DateTime.class)).isTrue();
        assertThat(typeConverter.canConvert(java.sql.Date.class, DateTime.class)).isTrue();
        assertThat(typeConverter.canConvert(Object.class, DateTime.class)).isFalse();
        assertThat(typeConverter.canConvert(Date.class, String.class)).isFalse();
        assertThat(typeConverter.canConvert(Gender.class, String.class)).isFalse();
    }

    @Test
    public void testConvert() throws Exception {
        assertThat(typeConverter.convert(null, DateTime.class)).isNull();
        assertThat(typeConverter.convert(dt1.toDate(), DateTime.class)).isEqualTo(dt1);
        assertThat(typeConverter.convert(dt2.toDate(), DateTime.class)).isEqualTo(dt2);
        assertThat(typeConverter.convert(dt3.toDate(), DateTime.class)).isEqualTo(dt3);
    }

    @Test
    public void testReverse() throws Exception {
        assertThat(typeConverter.reverse(null, Date.class)).isNull();
        assertThat(typeConverter.reverse(dt1, Date.class)).isEqualTo(dt1.toDate());
        assertThat(typeConverter.reverse(dt2, Date.class)).isEqualTo(dt2.toDate());
        assertThat(typeConverter.reverse(dt3, Date.class)).isEqualTo(dt3.toDate());
    }
}

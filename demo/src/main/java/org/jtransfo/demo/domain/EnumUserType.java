/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.demo.domain;

import org.hibernate.HibernateException;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

/**
 * Custom mapping for enum using {@link Enum#name()}..
 */
public class EnumUserType implements EnhancedUserType, ParameterizedType {

    private Class<Enum> enumClass;

    @Override
    public void setParameterValues(Properties parameters) {
        String enumClassName = parameters.getProperty("enumClassName");
        try {
            enumClass = (Class<Enum>) Class.forName(enumClassName);
        } catch (ClassNotFoundException cnfe) {
            throw new HibernateException("Enum class not found", cnfe);
        }
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return cached;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Enum) value;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x == y;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, Object o)
            throws HibernateException, SQLException {
        String name = resultSet.getString(names[0]);
        return resultSet.wasNull() ? null : Enum.valueOf(enumClass, name);
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index)
            throws HibernateException, SQLException {
        if (value == null) {
            preparedStatement.setNull(index, Types.VARCHAR);
        } else {
            preparedStatement.setString(index, ((Enum) value).name());
        }
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return original;
    }

    @Override
    public Class returnedClass() {
        return enumClass;
    }

    @Override
    public int[] sqlTypes() {
        return new int[]{Types.VARCHAR};
    }

    @Override
    public Object fromXMLString(String xmlValue) {
        return Enum.valueOf(enumClass, xmlValue);
    }

    @Override
    public String objectToSQLString(Object value) {
        return '\'' + ((Enum) value).name() + '\'';
    }

    @Override
    public String toXMLString(Object value) {
        return ((Enum) value).name();
    }
}
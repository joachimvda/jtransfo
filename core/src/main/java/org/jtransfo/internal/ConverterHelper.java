/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache
 * License, Version 2.0. All contributions in this program are covered
 * by the Geomajas Contributors License Agreement. For full licensing
 * details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.MappedBy;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.NotMapped;
import org.jtransfo.ToConverter;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for building the converters for a pair of classes.
 */
public class ConverterHelper {

    private ConcurrentHashMap<String, TypeConverter> typeConverters = new ConcurrentHashMap<String, TypeConverter>();

    /**
     * Build the descriptor for conversion between given object types.
     *
     * @param toClass transfer object class, contains the annotations for the conversion
     * @param domainClass domain class as other side of conversion
     * @return conversion descriptor
     * @throws JTransfoException cannot build converter
     */
    public ToConverter getToConverter(Class toClass, Class domainClass) throws JTransfoException {
        ToConverter converter = new ToConverter();

        List<Field> domainFields = getFields(domainClass);
        for (Field field : getFields(toClass)) {
            if (!Modifier.isTransient(field.getModifiers()) && (null != field.getAnnotation(NotMapped.class))) {
                MappedBy mappedBy = field.getAnnotation(MappedBy.class);

                String domainFieldName = field.getName();
                if (null != mappedBy && !MappedBy.DEFAULT_FIELD.equals(mappedBy.field())) {
                    domainFieldName = mappedBy.field();
                }
                Field domainField = findField(domainFields, domainFieldName);
                if (null == domainField) {
                    throw new JTransfoException(
                            "Cannot determine mapping for field " + field.getName() + " in class " + toClass.getName() +
                                    ". The field " + domainFieldName + " in class " + domainClass.getName() +
                                    " cannot be found.");
                }
                TypeConverter typeConverter = getDeclaredTypeConverter(mappedBy);
                if (null == typeConverter) {
                    typeConverter = getDefaultTypeConverter(field.getType(), domainField.getType());
                }
                converter.getToTo().add(new ToToConverter(field, domainField, typeConverter));
                if (null == mappedBy || !mappedBy.readOnly()) {
                    converter.getToDomain().add(new ToDomainConverter(field, domainField, typeConverter));
                }
            }
        }

        return converter;
    }

    /**
     * Find one field in a list of fields.
     *
     * @param fields list of fields
     * @param fieldName field to search
     * @return field with requested name or null when not found
     */
    protected Field findField(List<Field> fields, String fieldName) {
        for (Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Get the {@link TypeConverter} which was specified in the {@link MappedBy} annotation (if any).
     *
     * @param mappedBy annotation
     * @return declared type converter
     */
    protected TypeConverter getDeclaredTypeConverter(MappedBy mappedBy) {
        if (null == mappedBy) {
            return null;
        }
        String typeConverterClass = mappedBy.typeConverterClass().getName();
        if (MappedBy.DefaultTypeConverter.class.getName().equals(typeConverterClass)) {
            typeConverterClass = mappedBy.typeConverter();
        }
        if (!MappedBy.DEFAULT_TYPE_CONVERTER.equals(typeConverterClass)) {
            TypeConverter typeConverter = typeConverters.get(typeConverterClass);
            if (null == typeConverter) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                if (null == classLoader) {
                    classLoader = getClass().getClassLoader();
                }
                try {
                    typeConverter = (TypeConverter) classLoader.loadClass(typeConverterClass).newInstance();
                } catch (ClassNotFoundException cnfe) {
                    throw new JTransfoException("Declared TypeConverter class " + typeConverterClass +
                            " cannot be found.", cnfe);
                } catch (InstantiationException ie) {
                    throw new JTransfoException("Declared TypeConverter class " + typeConverterClass +
                            " cannot be instantiated.", ie);
                } catch (IllegalAccessException iae) {
                    throw new JTransfoException("Declared TypeConverter class " + typeConverterClass +
                            " cannot be accessed.", iae);
                } catch (ClassCastException cce) {
                    throw new JTransfoException("Declared TypeConverter class " + typeConverterClass +
                            " cannot be cast to a TypeConverter.", cce);
                }
            }
            return typeConverter;
        }
        return null;
    }

    /**
     * Get the default type converter given the field types to cnvert between.
     *
     * @param toField transfer object field class
     * @param domainField domain object field class
     * @return type converter
     */
    protected TypeConverter getDefaultTypeConverter(Class<?> toField, Class<?> domainField) {
        return new NoConversionTypeConverter(); // @todo no type conversion for now
    }

    /**
     * Find all declared fields of a class. Fields which are hidden by a child class are not included.
     *
     * @param clazz class to find fields for
     * @return list of fields
     */
    protected List<Field> getFields(Class<?> clazz) {
        List<Field> result = new ArrayList<Field>();
        Set<String> fieldNames = new HashSet<String>();
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (!fieldNames.contains(field.getName())) {
                    fieldNames.add(field.getName());
                    makeAccessible(field);
                    result.add(field);
                }
            }
            searchType = searchType.getSuperclass();
        }
        return result;
    }

    /**
     * Make the given field accessible, explicitly setting it accessible if necessary.
     * The <code>setAccessible(true)</code> method is only called when actually necessary, to avoid unnecessary
     * conflicts with a JVM SecurityManager (if active).
     * <p/>
     * This method is borrowed from Spring's ReflectionUtil class.
     *
     * @param field the field to make accessible
     * @see java.lang.reflect.Field#setAccessible
     */
    protected static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
                Modifier.isFinal(field.getModifiers())) && !field.isAccessible()) {
            field.setAccessible(true);
        }
    }

}

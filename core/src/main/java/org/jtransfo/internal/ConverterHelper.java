/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.MappedBy;
import org.jtransfo.Named;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.NotMapped;
import org.jtransfo.ToConverter;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for building the converters for a pair of classes.
 */
public class ConverterHelper {

    private static final String DECLARED_TYPE_CONVERTER_CLASS = "Declared TypeConverter class ";

    private ReflectionHelper reflectionHelper = new ReflectionHelper();
    private ConcurrentHashMap<String, TypeConverter> typeConverterInstances =
            new ConcurrentHashMap<String, TypeConverter>();
    private List<TypeConverter> typeConvertersInOrder = Collections.emptyList(); // empty list for starters

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

        List<SyntheticField> domainFields = reflectionHelper.getSyntheticFields(domainClass);
        for (Field field : reflectionHelper.getFields(toClass)) {
            boolean isTransient = Modifier.isTransient(field.getModifiers());
            NotMapped notMapped = field.getAnnotation(NotMapped.class);
            if (!isTransient && (null == notMapped)) {
                MappedBy mappedBy = field.getAnnotation(MappedBy.class);

                String domainFieldName = field.getName();
                String[] domainFieldPath = new String[0];
                boolean readOnlyField = false;
                if (null != mappedBy) {
                    if (!MappedBy.DEFAULT_FIELD.equals(mappedBy.field())) {
                        domainFieldName = mappedBy.field();
                    }
                    if (!MappedBy.DEFAULT_PATH.equals(mappedBy.path())) {
                        domainFieldPath = mappedBy.path().split("\\.");
                    }
                    readOnlyField = mappedBy.readOnly();
                }
                SyntheticField[] domainField;
                try {
                    domainField = findField(domainFields, domainFieldName, domainFieldPath, domainClass, readOnlyField);
                } catch (JTransfoException jte) {
                    throw new JTransfoException(
                            "Cannot determine mapping for field " + field.getName() + " in class " + toClass.getName() +
                                    ". The field " + domainFieldName + " in class " + domainClass.getName() + " " +
                                    withPath(domainFieldPath) + "cannot be found.", jte);
                }
                TypeConverter typeConverter = getDeclaredTypeConverter(mappedBy);
                if (null == typeConverter) {
                    typeConverter = getDefaultTypeConverter(field.getType(),
                            domainField[domainField.length - 1].getType());
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
     * Convert path array to a readable representation.
     *
     * @param path array of path elements
     * @return original path string
     */
    public String withPath(String[] path) {
        StringBuilder sb = new StringBuilder();
        if (path.length > 0) {
            sb.append(" (with path ");
            for (int i = 0; i < path.length - 1; i++) {
                sb.append(path[i]);
                sb.append(".");
            }
            sb.append(path[path.length - 1]);
            sb.append(") ");
        }
        return sb.toString();

    }

    /**
     * Find one field in a list of fields.
     *
     * @param domainFields list of fields in domain object
     * @param fieldName field to search
     * @param path list of intermediate fields for transitive fields
     * @param type type of object to find fields in
     * @param readOnlyField is the requested field read only
     * @return field with requested name or null when not found
     * @throws JTransfoException cannot find fields
     */
    protected SyntheticField[] findField(List<SyntheticField> domainFields, String fieldName, String[] path,
            Class<?> type, boolean readOnlyField) throws JTransfoException {
        List<SyntheticField> fields = domainFields;
        SyntheticField[] result = new SyntheticField[path.length + 1];
        int index = 0;
        Class<?> currentType = type;
        for (; index < path.length; index++) {
            boolean found = false;
            for (SyntheticField field : fields) {
                if (field.getName().equals(path[index])) {
                    found = true;
                    result[index] = field;
                    break;
                }
            }
            if (!found) {
                result[index] = new AccessorSyntheticField(reflectionHelper, currentType, path[index], readOnlyField);
            }
            currentType = result[index].getType();
            fields = reflectionHelper.getSyntheticFields(currentType);
        }
        for (SyntheticField field : fields) {
            if (field.getName().equals(fieldName)) {
                result[index] = field;
                return result;
            }
        }
        result[index] = new AccessorSyntheticField(reflectionHelper, currentType, fieldName, readOnlyField);
        return result;
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
            TypeConverter typeConverter = typeConverterInstances.get(typeConverterClass);
            if (null == typeConverter) {
                try {
                    typeConverter = reflectionHelper.<TypeConverter>newInstance(typeConverterClass);
                    typeConverterInstances.put(typeConverterClass, typeConverter);
                } catch (ClassNotFoundException cnfe) {
                    throw new JTransfoException(DECLARED_TYPE_CONVERTER_CLASS + typeConverterClass +
                            " cannot be found.", cnfe);
                } catch (InstantiationException ie) {
                    throw new JTransfoException(DECLARED_TYPE_CONVERTER_CLASS + typeConverterClass +
                            " cannot be instantiated.", ie);
                } catch (IllegalAccessException iae) {
                    throw new JTransfoException(DECLARED_TYPE_CONVERTER_CLASS + typeConverterClass +
                            " cannot be accessed.", iae);
                } catch (ClassCastException cce) {
                    throw new JTransfoException(DECLARED_TYPE_CONVERTER_CLASS + typeConverterClass +
                            " cannot be cast to a TypeConverter.", cce);
                }
            }
            return typeConverter;
        }
        return null;
    }

    /**
     * Set the list of type converters. When searching a type conversion, the list is traversed front to back.
     *
     * @param typeConverters ordered list of type converters, the first converter in the list which can do the
     *      conversion is used.
     */
    public void setTypeConvertersInOrder(Collection<TypeConverter> typeConverters) {
        LockableList<TypeConverter> newList = new LockableList<TypeConverter>();
        newList.addAll(typeConverters);
        newList.lock();
        typeConvertersInOrder = newList;

        // update list of converters to allow mentioning type converter by name, class name is used if no name provided
        for (TypeConverter tc : newList) {
            String name = null;
            if (tc instanceof Named) {
                name = ((Named) tc).getName();
            }
            if (null == name) {
                name = tc.getClass().getName();
            }
            typeConverterInstances.put(name, tc);
        }
    }

    /**
     * Get the default type converter given the field types to convert between.
     *
     * @param toField transfer object field class
     * @param domainField domain object field class
     * @return type converter
     */
    protected TypeConverter getDefaultTypeConverter(Class<?> toField, Class<?> domainField) {
        for (TypeConverter typeConverter : typeConvertersInOrder) {
            if (typeConverter.canConvert(toField, domainField)) {
                return typeConverter;
            }
        }
        return new NoConversionTypeConverter(); // default to no type conversion
    }

}

/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.MapOnlies;
import org.jtransfo.MapOnly;
import org.jtransfo.MappedBy;
import org.jtransfo.Named;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.NotMapped;
import org.jtransfo.PostConvert;
import org.jtransfo.PostConverter;
import org.jtransfo.PreConvert;
import org.jtransfo.PreConverter;
import org.jtransfo.ToConverter;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper class for building the converters for a pair of classes.
 */
public class ConverterHelper {

    private static final String DECLARED_TYPE_CONVERTER_CLASS = "Declared TypeConverter class ";

    private ReflectionHelper reflectionHelper = new ReflectionHelper();
    private ConcurrentHashMap<String, TypeConverter> typeConverterInstances = new ConcurrentHashMap<>();
    private List<TypeConverter> typeConvertersInOrder = Collections.emptyList(); // empty list for starters
    private ConcurrentHashMap<String, PreConverter> preConverterInstances = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, PostConverter> postConverterInstances = new ConcurrentHashMap<>();

    /**
     * Build the descriptor for conversion between given object types.
     *
     * @param toClass transfer object class, contains the annotations for the conversion
     * @param domainClass domain class as other side of conversion
     * @return conversion descriptor
     * @throws JTransfoException cannot build converter
     */
    public ToConverter getToConverter(Class toClass, Class domainClass) throws JTransfoException {
        ToConverter converter = withPreConverter(toClass);

        List<SyntheticField> domainFields = reflectionHelper.getSyntheticFields(domainClass);
        for (Field field : reflectionHelper.getFields(toClass)) {
            boolean isTransient = Modifier.isTransient(field.getModifiers());
            List<NotMapped> notMapped = reflectionHelper.getAnnotationWithMeta(field, NotMapped.class);
            if (!isTransient && (0 == notMapped.size())) {
                List<MappedBy> mappedBies = reflectionHelper.getAnnotationWithMeta(field, MappedBy.class);
                if (mappedBies.size() > 1) {
                    throw new JTransfoException("Field " + field.getName() + " on type " +
                            field.getDeclaringClass().getName() +
                            " MappedBy is ambiguous, check your meta-annotations.");
                }
                MappedBy mappedBy = null;
                if (1 == mappedBies.size()) {
                    mappedBy = mappedBies.get(0);
                }

                boolean isStatic = (0 != (field.getModifiers() & Modifier.STATIC));
                if (0 != mappedBies.size() || !isStatic) {
                    buildConverters(field, domainFields, domainClass, converter, mappedBy);
                }
            }
        }

        addPostConverter(converter, toClass);
        return converter;
    }

    private SyntheticField[] getDomainField(Field field, List<SyntheticField> domainFields, Class domainClass,
            String fieldParam, String pathParam, boolean readOnlyParam) {
        String domainFieldName = field.getName();
        String[] domainFieldPath = new String[0];
        boolean readOnlyField = (0 != (field.getModifiers() & Modifier.FINAL)) || readOnlyParam; // final -> read-only
        if (!MappedBy.DEFAULT_FIELD.equals(fieldParam)) {
            domainFieldName = fieldParam;
        }
        if (!MappedBy.DEFAULT_PATH.equals(pathParam)) {
            domainFieldPath = pathParam.split("\\.");
        }
        SyntheticField[] domainField;
        try {
            domainField = findField(domainFields, domainFieldName, domainFieldPath, domainClass,
                    readOnlyField);
        } catch (JTransfoException jte) {
            throw new JTransfoException(String.format("Cannot determine mapping for field %s in class " +
                            "%s. The field %s in class %s %scannot be found.",
                    field.getName(), field.getDeclaringClass().getName(),
                    domainFieldName, domainClass.getName(), withPath(domainFieldPath)), jte);
        }
        return domainField;
    }

    private SyntheticField[] getDomainField(Field field, List<SyntheticField> domainFields, Class domainClass,
            MappedBy mappedBy) {
        if (null == mappedBy) {
            return getDomainField(field, domainFields, domainClass,
                    MappedBy.DEFAULT_FIELD, MappedBy.DEFAULT_PATH, false);
        } else {
            return getDomainField(field, domainFields, domainClass,
                    mappedBy.field(), mappedBy.path(), mappedBy.readOnly());
        }
    }

    private void buildConverters(Field field, List<SyntheticField> domainFields, Class domainClass,
            ToConverter converter, MappedBy mappedBy) {
        SyntheticField sField = new SimpleSyntheticField(field);
        List<MapOnly> mapOnlies = getMapOnlies(field);
        if (null == mapOnlies) {
            SyntheticField[] domainField = getDomainField(field, domainFields, domainClass, mappedBy);
            TypeConverter typeConverter = getDeclaredTypeConverter(mappedBy);
            if (null == typeConverter) {
                typeConverter = getDefaultTypeConverter(field.getGenericType(),
                        domainField[domainField.length - 1].getGenericType());
            }
            if (0 == (field.getModifiers() & Modifier.FINAL)) { // cannot write final fields
                converter.getToTo().add(new ToToConverter(sField, domainField, typeConverter));
            }
            if (null == mappedBy || !mappedBy.readOnly()) {
                converter.getToDomain().add(new ToDomainConverter(sField, domainField, typeConverter));
            }
        } else {
            TaggedConverter toTo = new TaggedConverter();
            TaggedConverter toDomain = new TaggedConverter();
            converter.getToTo().add(toTo);
            converter.getToDomain().add(toDomain);

            for (MapOnly mapOnly : mapOnlies) {
                // determine new domain field if path or field declare on mapOnly
                SyntheticField[] mapOnlyDomainField = null;
                if (!MappedBy.DEFAULT_PATH.equals(mapOnly.path()) || !MappedBy.DEFAULT_FIELD.equals(mapOnly.field())) {
                    mapOnlyDomainField = getDomainField(field, domainFields, domainClass,
                            mapOnly.field(), mapOnly.path(), mapOnly.readOnly());
                }
                if (null == mapOnlyDomainField) {
                    mapOnlyDomainField = getDomainField(field, domainFields, domainClass, mappedBy);
                }
                TypeConverter typeConverter = getDeclaredTypeConverter(mappedBy);
                if (null == typeConverter) {
                    typeConverter = getDefaultTypeConverter(field.getGenericType(),
                            mapOnlyDomainField[mapOnlyDomainField.length - 1].getGenericType());
                }
                TypeConverter moTypeConverter = getDeclaredTypeConverter(mapOnly, typeConverter);
                ToToConverter ttc = new ToToConverter(sField, mapOnlyDomainField, moTypeConverter);
                toTo.addConverters(ttc, mapOnly.value());
                if (!mapOnly.readOnly()) {
                    ToDomainConverter tdc = new ToDomainConverter(sField, mapOnlyDomainField, moTypeConverter);
                    toDomain.addConverters(tdc, mapOnly.value());
                }
            }
        }
    }

    private ToConverter withPreConverter(Class toClass) {
        List<PreConvert.List> preConvertListAnnotations =
                reflectionHelper.getAnnotationWithMeta(toClass, PreConvert.List.class);
        List<PreConvert> preConvertAnnotations = reflectionHelper.getAnnotationWithMeta(toClass, PreConvert.class);
        preConvertListAnnotations.forEach(list -> preConvertAnnotations.addAll(Arrays.asList(list.value())));
        if (preConvertAnnotations.isEmpty()) {
            return new ToConverter();
        } else {
            List<PreConverter> preConverters = new ArrayList<>();
            for (PreConvert ann : preConvertAnnotations) {
                preConverters.add(
                        getConverter(ann.value(), ann.converterClass(), preConverterInstances, "preConverter"));
            }
            if (preConverters.size() == 1) {
                return new ToConverter(preConverters.get(0));
            } else {
                return new ToConverter(new CombinedPreConverter(preConverters));
            }
        }
    }

    private void addPostConverter(ToConverter converter, Class toClass) {
        List<PostConvert.List> postConvertListAnnotations =
                reflectionHelper.getAnnotationWithMeta(toClass, PostConvert.List.class);
        List<PostConvert> postConvertAnnotations = reflectionHelper.getAnnotationWithMeta(toClass, PostConvert.class);
        postConvertListAnnotations.forEach(list -> postConvertAnnotations.addAll(Arrays.asList(list.value())));
        for (PostConvert ann : postConvertAnnotations) {
            PostConverter postConverter =
                    getConverter(ann.value(), ann.converterClass(), postConverterInstances, "postConverter");
            converter.addToDomain(postConverter::postConvertToDomain);
            converter.addToTo(postConverter::postConvertToTo);
        }
    }

    private <C> C getConverter(String converterName, Class converterClass,
            Map<String, C> converterInstances, String typeForException) {
        String name = converterName;
        if (PreConvert.DEFAULT_NAME.equals(name)) {
            name = converterClass.getName();
        }
        C converter = converterInstances.get(name);
        if (null == converter) {
            throw new JTransfoException(String.format("Cannot find %s %s.", typeForException, name));
        }
        return converter;
    }

    /**
     * Get the @MapOnly definitions which exist on a field, be it an individual {@link MapOnly} annotation or grouped
     * in {@link MapOnlies} or both.
     *
     * @param field field to get annotations for
     * @return list of annotations
     */
    List<MapOnly> getMapOnlies(Field field) {
        List<MapOnly> mapOnly = reflectionHelper.getAnnotationWithMeta(field, MapOnly.class);
        List<MapOnlies> mapOnlies = reflectionHelper.getAnnotationWithMeta(field, MapOnlies.class);
        if (0 == mapOnly.size() && 0 == mapOnlies.size()) {
            return null;
        }
        List<MapOnly> res = new ArrayList<>();
        res.addAll(mapOnly);
        mapOnlies.forEach(mo -> Collections.addAll(res, mo.value()));
        return res;
    }

    /**
     * Convert path array to a readable representation.
     *
     * @param path array of path elements
     * @return original path string
     */
    String withPath(String[] path) {
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
    SyntheticField[] findField(List<SyntheticField> domainFields, String fieldName, String[] path,
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
     * @param typeConverterClassName1 highest priority type converter name (from class in annotation)
     * @param typeConverterClassName2  second highest priority type converter name (FQN)
     * @return type converter with highest precedence
     */
    TypeConverter getDeclaredTypeConverter(String typeConverterClassName1, String typeConverterClassName2) {
        String typeConverterClass = typeConverterClassName1;
        if (MappedBy.DefaultTypeConverter.class.getName().equals(typeConverterClass)) {
            typeConverterClass = typeConverterClassName2;
        }
        if (!MappedBy.DEFAULT_TYPE_CONVERTER.equals(typeConverterClass)) {
            TypeConverter typeConverter = typeConverterInstances.get(typeConverterClass);
            if (null == typeConverter) {
                try {
                    typeConverter = reflectionHelper.newInstance(typeConverterClass);
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
     * Get declared type converter for a @MappedBy annotation.
     *
     * @param mappedBy MappedBy annotation
     * @return type converter with highest precedence
     */
    TypeConverter getDeclaredTypeConverter(MappedBy mappedBy) {
        if (null == mappedBy) {
            return null;
        }
        return getDeclaredTypeConverter(mappedBy.typeConverterClass().getName(), mappedBy.typeConverter());
    }

    /**
     * Determine type converter for a @MapOnly annotation (falling back to @MappedBy type converter.
     *
     * @param mapOnly MapOnly annotation
     * @param fallback type converter from MappedBy annotation
     * @return type converter with highest precedence
     */
     TypeConverter getDeclaredTypeConverter(MapOnly mapOnly, TypeConverter fallback) {
        if (null != mapOnly) {
            TypeConverter typeConverter = getDeclaredTypeConverter(
                    mapOnly.typeConverterClass().getName(), mapOnly.typeConverter());
            if (null != typeConverter) {
                return typeConverter;
            }
        }
        return fallback;
    }

    /**
     * Set the list of type converters. When searching a type conversion, the list is traversed front to back.
     *
     * @param typeConverters ordered list of type converters, the first converter in the list which can do the
     *      conversion is used.
     */
    void setTypeConvertersInOrder(Collection<TypeConverter> typeConverters) {
        LockableList<TypeConverter> newList = new LockableList<>();
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
    TypeConverter getDefaultTypeConverter(Type toField, Type domainField) {
        for (TypeConverter typeConverter : typeConvertersInOrder) {
            if (typeConverter.canConvert(toField, domainField)) {
                return typeConverter;
            }
        }
        return new NoConversionTypeConverter(); // default to no type conversion
    }

    /**
     * Set the list of type converters. When searching a type conversion, the list is traversed front to back.
     *
     * @param preConverters ordered list of type converters, the first converter in the list which can do the
     *      conversion is used.
     */
    void setPreConverters(Collection<PreConverter> preConverters) {
        for (PreConverter pc : preConverters) {
            // register using specific name (if any)
            if (pc instanceof Named) {
                preConverterInstances.put(((Named) pc).getName(), pc);
            }
            // always register using class name
            preConverterInstances.put(pc.getClass().getName(), pc);
        }
    }

    /**
     * Set the list of type converters. When searching a type conversion, the list is traversed front to back.
     *
     * @param postConverters ordered list of type converters, the first converter in the list which can do the
     *      conversion is used.
     */
    void setPostConverters(Collection<PostConverter> postConverters) {
        for (PostConverter pc : postConverters) {
            // register using specific name (if any)
            if (pc instanceof Named) {
                postConverterInstances.put(((Named) pc).getName(), pc);
            }
            // always register using class name
            postConverterInstances.put(pc.getClass().getName(), pc);
        }
    }

}

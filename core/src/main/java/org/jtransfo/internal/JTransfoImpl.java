/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.AutomaticListTypeConverter;
import org.jtransfo.AutomaticSetTypeConverter;
import org.jtransfo.ClassReplacer;
import org.jtransfo.ConfigurableJTransfo;
import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.jtransfo.Converter;
import org.jtransfo.JTransfo;
import org.jtransfo.JTransfoException;
import org.jtransfo.NeedsJTransfo;
import org.jtransfo.NoConversionTypeConverter;
import org.jtransfo.ObjectFinder;
import org.jtransfo.ObjectReplacer;
import org.jtransfo.PostConverter;
import org.jtransfo.PreConverter;
import org.jtransfo.ReadOnlyDomainAutomaticTypeConverter;
import org.jtransfo.ToConverter;
import org.jtransfo.ToDomainTypeConverter;
import org.jtransfo.TypeConverter;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jTransfo main access point standard implementation.
 */
public class JTransfoImpl implements JTransfo, ConfigurableJTransfo, ConvertSourceTarget, ClassReplacer {

    private static final String[] DEFAULT_TAGS_WHEN_NO_TAGS = {JTransfo.DEFAULT_TAG_WHEN_NO_TAGS};

    private ToHelper toHelper = new ToHelper();
    private ConverterHelper converterHelper = new ConverterHelper();
    private Map<Class, ToConverter> converters = new ConcurrentHashMap<>();
    private List<ObjectFinder> modifyableObjectFinders = new ArrayList<>();
    private List<ObjectFinder> internalObjectFinders = new ArrayList<>();
    private LockableList<ObjectFinder> objectFinders = new LockableList<>();
    private List<TypeConverter> modifyableTypeConverters = new ArrayList<>();
    private List<TypeConverter> internalTypeConverters = new ArrayList<>();
    private List<PreConverter> modifyablePreConverters = new ArrayList<>();
    private List<PostConverter> modifyablePostConverters = new ArrayList<>();
    private List<ConvertInterceptor> modifyableConvertInterceptors = new ArrayList<>();
    private ConvertSourceTarget convertInterceptorChain;
    private List<ObjectReplacer> modifyableObjectReplacers = new ArrayList<>();
    private List<ClassReplacer> modifyableClassReplacers = new ArrayList<>();
    private LockableList<ObjectReplacer> objectReplacers = new LockableList<>();
    private LockableList<ClassReplacer> classReplacers = new LockableList<>();

    /**
     * Constructor.
     */
    public JTransfoImpl() {
        internalObjectFinders.add(new NewInstanceObjectFinder());
        updateObjectFinders();

        internalTypeConverters.add(new NoConversionTypeConverter());
        internalTypeConverters.add(new ToDomainTypeConverter(this));
        ReadOnlyDomainAutomaticTypeConverter readOnlyDomainAutomaticTypeConverter =
                new ReadOnlyDomainAutomaticTypeConverter();
        readOnlyDomainAutomaticTypeConverter.setSortList(true);
        internalTypeConverters.add(readOnlyDomainAutomaticTypeConverter);
        internalTypeConverters.add(new AutomaticSetTypeConverter());
        AutomaticListTypeConverter automaticListTypeConverter = new AutomaticListTypeConverter();
        automaticListTypeConverter.setSortList(true);
        internalTypeConverters.add(automaticListTypeConverter);
        updateTypeConverters();

        updateConvertInterceptors();

        updateObjectReplacers();

        updateClassReplacers();

        // CHECKSTYLE EMPTY_BLOCK: OFF
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> plugin = cl.loadClass("org.jtransfo.internal.JTransfoJrebelPlugin");
            Method setInstance = plugin.getMethod("setInstance", JTransfoImpl.class);
            Method preInit = plugin.getMethod("preinit");
            if (null != setInstance && null != preInit) {
                Object instance = plugin.newInstance();
                setInstance.invoke(instance, this);
                preInit.invoke(instance);
            }
            System.out.println("jRebel reload support for jTransfo loaded.");
        } catch (Throwable ex) {
            // JRebel not found - will not reload directory service automatically.
        }
        // CHECKSTYLE EMPTY_BLOCK: ON
    }

    @Override
    public List<TypeConverter> getTypeConverters() {
        return modifyableTypeConverters;
    }

    @Override
    public void updateTypeConverters() {
        updateTypeConverters(null);
    }

    @Override
    public void updateTypeConverters(List<TypeConverter> newConverters) {
        if (null != newConverters) {
            modifyableTypeConverters.clear();
            modifyableTypeConverters.addAll(newConverters);

        }
        ArrayList<TypeConverter> allConverters = new ArrayList<>();
        allConverters.addAll(modifyableTypeConverters);
        allConverters.addAll(internalTypeConverters);
        for (TypeConverter tc : allConverters) {
            if (tc instanceof NeedsJTransfo) {
                ((NeedsJTransfo) tc).setJTransfo(this);
            }
        }
        converterHelper.setTypeConvertersInOrder(allConverters);
    }

    @Override
    public List<PreConverter> getPreConverters() {
        return modifyablePreConverters;
    }

    @Override
    public void updatePreConverters() {
        updatePreConverters(null);
    }

    @Override
    public void updatePreConverters(List<PreConverter> newConverters) {
        if (null != newConverters) {
            modifyablePreConverters.clear();
            modifyablePreConverters.addAll(newConverters);
        }
        ArrayList<PreConverter> allConverters = new ArrayList<>();
        allConverters.addAll(modifyablePreConverters);
        for (PreConverter pc : allConverters) {
            if (pc instanceof NeedsJTransfo) {
                ((NeedsJTransfo) pc).setJTransfo(this);
            }
        }
        converterHelper.setPreConverters(allConverters);
    }

    @Override
    public List<PostConverter> getPostConverters() {
        return modifyablePostConverters;
    }

    @Override
    public void updatePostConverters() {
        updatePostConverters(null);
    }

    @Override
    public void updatePostConverters(List<PostConverter> newConverters) {
        if (null != newConverters) {
            modifyablePostConverters.clear();
            modifyablePostConverters.addAll(newConverters);

        }
        ArrayList<PostConverter> allConverters = new ArrayList<>();
        allConverters.addAll(modifyablePostConverters);
        for (PostConverter pc : allConverters) {
            if (pc instanceof NeedsJTransfo) {
                ((NeedsJTransfo) pc).setJTransfo(this);
            }
        }
        converterHelper.setPostConverters(allConverters);
    }

    @Override
    public List<ObjectFinder> getObjectFinders() {
        return modifyableObjectFinders;
    }

    @Override
    public void updateObjectFinders() {
        updateObjectFinders(null);
    }

    @Override
    public void updateObjectFinders(List<ObjectFinder> newObjectFinders) {
        if (null != newObjectFinders) {
            modifyableObjectFinders.clear();
            modifyableObjectFinders.addAll(newObjectFinders);

        }
        LockableList<ObjectFinder> newList = new LockableList<>();
        newList.addAll(internalObjectFinders);
        newList.addAll(modifyableObjectFinders);
        newList.lock();
        objectFinders = newList;
    }

    @Override
    public List<ObjectReplacer> getObjectReplacers() {
        return modifyableObjectReplacers;
    }

    @Override
    public void updateObjectReplacers() {
        updateObjectReplacers(null);
    }

    @Override
    public void updateObjectReplacers(List<ObjectReplacer> newObjectReplacers) {
        if (null != newObjectReplacers) {
            modifyableObjectReplacers.clear();
            modifyableObjectReplacers.addAll(newObjectReplacers);
        }
        LockableList<ObjectReplacer> newList = new LockableList<>();
        newList.addAll(modifyableObjectReplacers);
        newList.lock();
        objectReplacers = newList;
    }

    @Override
    public List<ClassReplacer> getClassReplacers() {
        return modifyableClassReplacers;
    }

    @Override
    public void updateClassReplacers() {
        updateClassReplacers(null);
    }

    @Override
    public void updateClassReplacers(List<ClassReplacer> newClassReplacers) {
        if (null != newClassReplacers) {
            modifyableClassReplacers.clear();
            modifyableClassReplacers.addAll(newClassReplacers);
        }
        LockableList<ClassReplacer> newList = new LockableList<>();
        newList.addAll(modifyableClassReplacers);
        newList.lock();
        classReplacers = newList;
    }

    /**
     * Get the list of {@link ConvertInterceptor}s to allow customization.
     * <p>
     * The elements are tried in reverse order (from end to start of list).
     * </p><p>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * </p><p>
     * Changes in the list are not used until you call {@link #updateConvertInterceptors()}.
     * </p>
     *
     * @return list of object finders
     */
    public List<ConvertInterceptor> getConvertInterceptors() {
        return modifyableConvertInterceptors;
    }

    /**
     * Update the list of convert interceptors which is used based on the internal list
     * (see {@link #getConvertInterceptors()}.
     */
    public void updateConvertInterceptors() {
        updateConvertInterceptors(null);
    }

    /**
     * Update the list of convert interceptors which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getConvertInterceptors()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newConvertInterceptors new list of convert interceptors
     */
    public void updateConvertInterceptors(List<ConvertInterceptor> newConvertInterceptors) {
        if (null != newConvertInterceptors) {
            modifyableConvertInterceptors.clear();
            modifyableConvertInterceptors.addAll(newConvertInterceptors);

        }
        ConvertSourceTarget chainPart = this; // actual conversion at the end of the list
        for (int i = modifyableConvertInterceptors.size() - 1; i >= 0; i--) {
            chainPart = new ConvertInterceptorChainPiece(modifyableConvertInterceptors.get(i), chainPart);
        }
        convertInterceptorChain = chainPart;
    }

    @Override
    public <T> T convert(Object source, T target, String... tags) {
        if (null == source || null == target) {
            throw new JTransfoException("Source and target are required to be not-null.");
        }
        source = replaceObject(source);
        boolean targetIsTo = false;
        if (!toHelper.isTo(source)) {
            targetIsTo = true;
            if (!toHelper.isTo(target)) {
                throw new JTransfoException(String.format("Neither source nor target are annotated with DomainClass " +
                        "on classes %s and %s.", source.getClass().getName(), target.getClass().getName()));
            }
        }
        if (0 == tags.length) {
            tags = DEFAULT_TAGS_WHEN_NO_TAGS;
        }

        return convertInterceptorChain.convert(source, target, targetIsTo, tags);
    }

    @Override
    public <T> T convert(Object source, T target, boolean targetIsTo, String... tags) {
        source = replaceObject(source);
        ToConverter toConverter;
        List<Converter> converters;
        PreConverter.Result shouldConvert;
        if (targetIsTo) {
            toConverter = getToConverter(target.getClass());
            converters = toConverter.getToTo();
            shouldConvert = toConverter.getPreConverter().preConvertToTo(source, target, tags);
        } else {
            toConverter = getToConverter(source.getClass());
            converters = toConverter.getToDomain();
            shouldConvert = toConverter.getPreConverter().preConvertToDomain(source, target, tags);
        }
        if (PreConverter.Result.CONTINUE == shouldConvert) {
            for (Converter converter : converters) {
                converter.convert(source, target, tags);
            }
        }
        return target;
    }

    @Override
    public Object convert(Object source) {
        if (null == source) {
            return null;
        }
        source = replaceObject(source);
        Class<?> domainClass = getDomainClass(source.getClass());
        return convertTo(source,  domainClass);
    }

    @Override
    public <T> T convertTo(Object source, Class<T> targetClass, String... tags) {
        if (null == source) {
            return null;
        }
        source = replaceObject(source);
        Class<T> realTargetClass = replaceClass(targetClass);
        return (T) convert(source, findTarget(source, realTargetClass, tags), tags);
    }

    @Override
    public <T> List<T> convertList(List<?> source, Class<T> targetClass, String... tags) {
        if (null == source) {
            return null;
        }
        List<T> result = new ArrayList<>();
        for (Object object : source) {
            result.add(convertTo(object, targetClass, tags));
        }
        return result;
    }

    @Override
    public <T> T findTarget(Object source, Class<T> targetClass, String... tags) {
        if (null == source) {
            return null;
        }
        source = replaceObject(source);
        int i = objectFinders.size() - 1;
        T target = null;
        Class<T> realTargetClass = replaceClass(targetClass);
        if (isToClass(realTargetClass)) {
            realTargetClass = (Class<T>) getToSubType(realTargetClass, source);
        }
        while (null == target && i >= 0) {
            target = objectFinders.get(i--).getObject(realTargetClass, source, tags);
        }
        if (null == target) {
            throw new JTransfoException("Cannot create instance of target class " + realTargetClass.getName() +
                    " for source object " + source + ".");
        }
        return target;
    }

    @Override
    public Class<?> getDomainClass(Class<?> toClass) {
        return toHelper.getDomainClass(toClass);
    }

    @Override
    public boolean isToClass(Class<?> toClass) {
        return toHelper.isToClass(toClass);
    }

    @Override
    public Class<?> getToSubType(Class<?> toType, Object domainObject) {
        return toHelper.getToSubType(toType, replaceObject(domainObject));
    }

    /**
     * Clear cache with converters. Needed when classes are reloaded by something like jRebel or spring reloaded.
     */
    public void clearCaches() {
        converters.clear();
    }

    private ToConverter getToConverter(Class toClass) {
        ToConverter toConverter = converters.get(toClass);
        if (null == toConverter) {
            Class<?> domainClass = getDomainClass(toClass);
            toConverter = converterHelper.getToConverter(toClass, domainClass);
            converters.put(toClass, toConverter);
        }
        return toConverter;
    }

    private Object replaceObject(Object object) {
        Object res = object;
        for (ObjectReplacer replacer : objectReplacers) {
            res = replacer.replaceObject(res);
        }
        return res;
    }

    @Override
    public Class replaceClass(Class clazz) {
        Class res = clazz;
        for (ClassReplacer replacer : classReplacers) {
            res = replacer.replaceClass(res);
        }
        return res;
    }

}

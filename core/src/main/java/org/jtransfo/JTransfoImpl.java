/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import org.jtransfo.internal.ConvertInterceptorChainPiece;
import org.jtransfo.internal.ConverterHelper;
import org.jtransfo.internal.LockableList;
import org.jtransfo.internal.NewInstanceObjectFinder;
import org.jtransfo.internal.ToHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * jTransfo main access point standard implementation.
 */
public class JTransfoImpl implements JTransfo, ConvertSourceTarget {

    private static final String[] DEFAULT_TAGS_WHEN_NO_TAGS = {JTransfo.DEFAULT_TAG_WHEN_NO_TAGS};

    private ToHelper toHelper = new ToHelper();
    private ConverterHelper converterHelper = new ConverterHelper();
    private Map<Class, ToConverter> converters = new ConcurrentHashMap<>();
    private List<ObjectFinder> modifyableObjectFinders = new ArrayList<>();
    private LockableList<ObjectFinder> objectFinders = new LockableList<>();
    private List<TypeConverter> modifyableTypeConverters = new ArrayList<>();
    private List<ConvertInterceptor> modifyableConvertInterceptors = new ArrayList<>();
    private ConvertSourceTarget convertInterceptorChain;
    private List<ObjectReplacer> modifyableObjectReplacers = new ArrayList<>();
    private LockableList<ObjectReplacer> objectReplacers = new LockableList<>();

    /**
     * Constructor.
     */
    public JTransfoImpl() {
        modifyableObjectFinders.add(new NewInstanceObjectFinder());
        updateObjectFinders();

        modifyableTypeConverters.add(new NoConversionTypeConverter());
        modifyableTypeConverters.add(new ToDomainTypeConverter(this));
        updateTypeConverters();

        updateConvertInterceptors();

        // CHECKSTYLE EMPTY_BLOCK: OFF
        try {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Class<?> plugin = cl.loadClass("org.jtransfo.JTransfoJrebelPlugin");
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

    /**
     * Get the set of type converters which are used by this jTransfo instance.
     * <p>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * </p><p>
     * Changes in the list are not used until you call {@link #updateTypeConverters()}.
     * </p>
     *
     * @return current list of type converters.
     */
    public List<TypeConverter> getTypeConverters() {
        return modifyableTypeConverters;
    }

    /**
     * Update the list of type converters which is used based on the internal list (see {@link #getTypeConverters()}.
     */
    public void updateTypeConverters() {
        updateTypeConverters(null);
    }

    /**
     * Update the list of type converters which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getTypeConverters()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newConverters new list of type converters
     */
    public void updateTypeConverters(List<TypeConverter> newConverters) {
        if (null != newConverters) {
            modifyableTypeConverters.clear();
            modifyableTypeConverters.addAll(newConverters);

        }
        for (TypeConverter tc : modifyableTypeConverters) {
            if (tc instanceof NeedsJTransfo) {
                ((NeedsJTransfo) tc).setJTransfo(this);
            }
        }
        converterHelper.setTypeConvertersInOrder(modifyableTypeConverters);
    }

    /**
     * Get the list of {@link ObjectFinder}s to allow customization.
     * <p>
     * The elements are tried in reverse order (from end to start of list).
     * </p><p>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * </p><p>
     * Changes in the list are not used until you call {@link #updateObjectFinders()}.
     * </p>
     *
     * @return list of object finders
     */
    public List<ObjectFinder> getObjectFinders() {
        return modifyableObjectFinders;
    }

    /**
     * Update the list of object finders which is used based on the internal list (see {@link #getObjectFinders()}.
     */
    public void updateObjectFinders() {
        updateObjectFinders(null);
    }

    /**
     * Update the list of object finders which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getObjectFinders()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newObjectFinders new list of type converters
     */
    public void updateObjectFinders(List<ObjectFinder> newObjectFinders) {
        if (null != newObjectFinders) {
            modifyableObjectFinders.clear();
            modifyableObjectFinders.addAll(newObjectFinders);

        }
        LockableList<ObjectFinder> newList = new LockableList<>();
        newList.addAll(modifyableObjectFinders);
        newList.lock();
        objectFinders = newList;
    }

    /**
     * Get the list of {@link ObjectReplacer}s to allow customization.
     * <p>
     * The elements are tried in order (from start to end of list).
     * </p><p>
     * You are explicitly allowed to change this list, but beware to do this from one thread only.
     * </p><p>
     * Changes in the list are not used until you call {@link #updateObjectReplacers()}.
     * </p>
     *
     * @return list of object replacers
     */
    public List<ObjectReplacer> getObjectReplacers() {
        return modifyableObjectReplacers;
    }

    /**
     * Update the list of object replacers which is used based on the internal list
     * (see {@link #getObjectReplacers()}.
     */
    public void updateObjectReplacers() {
        updateObjectReplacers(null);
    }

    /**
     * Update the list of object replacers which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getObjectReplacers()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newObjectReplacers new list of type converters
     */
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
        List<Converter> converters = targetIsTo ? getToToConverters(target.getClass()) :
                getToDomainConverters(source.getClass());
        for (Converter converter : converters) {
            converter.convert(source, target, tags);
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
        Class<T> realTarget = targetClass;
        if (null == source) {
            return null;
        }
        source = replaceObject(source);
        if (isToClass(targetClass)) {
            realTarget = (Class<T>) getToSubType(targetClass, source);
        }
        return (T) convert(source, findTarget(source, realTarget), tags);
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
    public <T> T findTarget(Object source, Class<T> targetClass) {
        if (null == source) {
            return null;
        }
        source = replaceObject(source);
        int i = objectFinders.size() - 1;
        T target = null;
        while (null == target && i >= 0) {
            target = objectFinders.get(i--).getObject(targetClass, source);
        }
        if (null == target) {
            throw new JTransfoException("Cannot create instance of target class " + targetClass.getName() +
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

    private List<Converter> getToToConverters(Class toClass) {
        return getToConverter(toClass).getToTo();
    }

    private List<Converter> getToDomainConverters(Class toClass) {
        return getToConverter(toClass).getToDomain();
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

}

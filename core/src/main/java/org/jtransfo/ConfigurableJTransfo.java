/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo;

import java.util.List;

/**
 * Extension of the {@JTransfo} interface which also includes the configuration settings
 * which are only needed for configuration/factories, not for normal use.
 */
public interface ConfigurableJTransfo extends JTransfo {

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
    List<TypeConverter> getTypeConverters();

    /**
     * Add the given type converter to this JTransfo instance.
     * <p>
     * This is a shorthand which adds the type converter to the {@link #getTypeConverters()} list and
     * calls {@link #updateTypeConverters()}.
     * </p>
     *
     * @param typeConverter type converter
     * @return this
     */
    default ConfigurableJTransfo with(TypeConverter typeConverter) {
        getTypeConverters().add(typeConverter);
        updateTypeConverters();
        return this;
    }

    /**
     * Update the list of type converters which is used based on the internal list
     * (see {@link #getTypeConverters()}.
     */
    void updateTypeConverters();

    /**
     * Update the list of type converters which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getTypeConverters()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newConverters new list of type converters
     */
    void updateTypeConverters(List<TypeConverter> newConverters);

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
    List<ObjectFinder> getObjectFinders();

    /**
     * Add the given object finder to this JTransfo instance.
     * <p>
     * This is a shorthand which adds the object finder to the {@link #getObjectFinders()} list and
     * calls {@link #updateObjectFinders()}.
     * </p>
     *
     * @param objectFinder object finder
     * @return this
     */
    default ConfigurableJTransfo with(ObjectFinder objectFinder) {
        getObjectFinders().add(objectFinder);
        updateObjectFinders();
        return this;
    }

    /**
     * Update the list of object finders which is used based on the internal list (see {@link #getObjectFinders()}.
     */
    void updateObjectFinders();

    /**
     * Update the list of object finders which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getObjectFinders()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newObjectFinders new list of type converters
     */
    void updateObjectFinders(List<ObjectFinder> newObjectFinders);

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
    List<ObjectReplacer> getObjectReplacers();

    /**
     * Add the given object replacer to this JTransfo instance.
     * <p>
     * This is a shorthand which adds the object finder to the {@link #getObjectFinders()} list and
     * calls {@link #updateObjectFinders()}.
     * </p>
     *
     * @param objectReplacer object replacer
     * @return this
     */
    default ConfigurableJTransfo with(ObjectReplacer objectReplacer) {
        getObjectReplacers().add(objectReplacer);
        updateObjectReplacers();
        return this;
    }

    /**
     * Update the list of object replacers which is used based on the internal list
     * (see {@link #getObjectReplacers()}.
     */
    void updateObjectReplacers();

    /**
     * Update the list of object replacers which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getObjectReplacers()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newObjectReplacers new list of type converters
     */
    void updateObjectReplacers(List<ObjectReplacer> newObjectReplacers);

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
    List<ConvertInterceptor> getConvertInterceptors();

    /**
     * Add the given convert interceptor to this JTransfo instance.
     * <p>
     * This is a shorthand which adds the convert interceptor to the {@link #getConvertInterceptors()} list and
     * calls {@link #updateConvertInterceptors()}.
     * </p>
     *
     * @param convertInterceptor convert interceptor
     * @return this
     */
    default ConfigurableJTransfo with(ConvertInterceptor convertInterceptor) {
        getConvertInterceptors().add(convertInterceptor);
        updateConvertInterceptors();
        return this;
    }

    /**
     * Update the list of convert interceptors which is used based on the internal list
     * (see {@link #getConvertInterceptors()}.
     */
    void updateConvertInterceptors();

    /**
     * Update the list of convert interceptors which is used.
     * <p>
     * When null is passed, this updates the changes to the internal list (see {@link #getConvertInterceptors()}.
     * Alternatively, you can pass the new list explicitly.
     * </p>
     *
     * @param newConvertInterceptors new list of convert interceptors
     */
    void updateConvertInterceptors(List<ConvertInterceptor> newConvertInterceptors);

}

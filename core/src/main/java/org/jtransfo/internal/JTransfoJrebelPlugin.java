/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.DomainClassDelegate;
import org.zeroturnaround.javarebel.ClassEventListener;
import org.zeroturnaround.javarebel.ClassResourceSource;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.Plugin;
import org.zeroturnaround.javarebel.ReloaderFactory;

/**
 * Jrebel plugin, assure jTransfo does not keep working with 'old' class definitions.
 */
public class JTransfoJrebelPlugin implements Plugin {

    private JTransfoImpl jTransfo;

    /**
     * Set instance for which this plugin (instance) applies.
     *
     * @param instance jTransfo registry instance
     */
    public void setInstance(JTransfoImpl instance) {
        jTransfo = instance;
    }

    @Override
    public void preinit() {
        registerListener();
    }

    private void registerListener() {
        // Set up the reload listener
        ReloaderFactory.getInstance().addClassReloadListener(
                new ClassEventListener() {
                    public void onClassEvent(int eventType, Class klass) {
                        try {
                            if (klass.isAnnotationPresent(DomainClass.class) ||
                                    klass.isAnnotationPresent(DomainClassDelegate.class)) {
                                jTransfo.clearCaches();
                            }
                        } catch (Exception e) {
                            LoggerFactory.getInstance().error(e);
                            System.out.println(e);
                        }
                    }

                    public int priority() {
                        return 0;
                    }
                }
        );
    }

    @Override
    public boolean checkDependencies(ClassLoader classLoader, ClassResourceSource classResourceSource) {
        return classResourceSource.getClassResource("org.jtransfo.internal.JTransfoImpl") != null;
    }

    @Override
    public String getId() {
        return "JTransfoJrebelPlugin";
    }

    @Override
    public String getName() {
        return "JRebel Plugin for jTransfo";
    }

    @Override
    public String getDescription() {
        return "Assure jTransfo notices changes in mapped classes - clear cache.";
    }

    @Override
    public String getAuthor() {
        return null;
    }

    @Override
    public String getWebsite() {
        return null;
    }

}

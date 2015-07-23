/*
 * This file is part of jTransfo, a library for converting to and from transfer objects.
 * Copyright (c) PROGS bvba, Belgium
 *
 * The program is available in open source according to the Apache License, Version 2.0.
 * For full licensing details, see LICENSE.txt in the project root.
 */

package org.jtransfo.internal;

import org.jtransfo.DomainClass;
import org.jtransfo.JTransfoException;
import org.jtransfo.SimpleObjectClassDeterminator;
import org.jtransfo.object.AbstractHumanTo;
import org.jtransfo.object.FemaleHumanDomain;
import org.jtransfo.object.FemaleHumanTo;
import org.jtransfo.object.MaleHumanDomain;
import org.jtransfo.object.MaleHumanTo;
import org.jtransfo.object.NoDomain;
import org.jtransfo.object.PersonTo;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class ToHelperTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private ToHelper toHelper = new ToHelper(new SimpleObjectClassDeterminator());

    @Test
    public void isToTest() throws Exception {
        assertThat(toHelper.isTo(new SimpleClassNameTo())).isTrue();
        assertThat(toHelper.isTo(new SimpleClassTypeTo())).isTrue();
        assertThat(toHelper.isTo(new SimpleClassDomain())).isFalse();
    }

    @Test
    public void testGetDomainClass() throws Exception {
        assertThat(toHelper.getDomainClass(SimpleClassNameTo.class)).isEqualTo(SimpleClassDomain.class);
        assertThat(toHelper.getDomainClass(SimpleClassTypeTo.class)).isEqualTo(SimpleClassDomain.class);
    }

    @Test
    public void testGetNoDomainClass() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage(" not annotated with DomainClass.");

        toHelper.getDomainClass(NoDomain.class);
    }

    @Test
    public void noClassDomainClassAnnotationTest() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage(" DomainClass annotation does not specify class.");

        toHelper.getDomainClass(NoClassTo.class);
    }

    @Test
    public void unknownClassDomainClassAnnotationTest() throws Exception {
        exception.expect(JTransfoException.class);
        exception.expectMessage(" DomainClass org.jtransfo.UnknownClass not found.");

        toHelper.getDomainClass(UnknownClassTo.class);
    }

    @Test
    public void getToSubTypeNoDomainClassDelegates() throws Exception {
        assertThat(toHelper.getToSubType(PersonTo.class, new PersonTo())).isEqualTo(PersonTo.class);
    }

    @Test
    public void getToSubTypeWithDomainClassDelegates() throws Exception {
        assertThat(toHelper.getToSubType(AbstractHumanTo.class, new FemaleHumanDomain())).
                isEqualTo(FemaleHumanTo.class);
        assertThat(toHelper.getToSubType(AbstractHumanTo.class, new MaleHumanDomain())).isEqualTo(MaleHumanTo.class);

        assertThat(toHelper.getToSubType(AbstractHumanTo.class, new Object())).isEqualTo(AbstractHumanTo.class);
        assertThat(toHelper.getToSubType(AbstractHumanTo.class, new MaleHumanDomain(){})).isEqualTo(MaleHumanTo.class);
    }

    @DomainClass
    private class NoClassTo {
    }

    @DomainClass(value = "org.jtransfo.UnknownClass")
    private class UnknownClassTo {
    }
}


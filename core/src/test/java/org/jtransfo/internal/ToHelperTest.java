package org.jtransfo.internal;

import org.jtransfo.JTransfoException;
import org.jtransfo.object.NoDomain;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.jtransfo.object.NoDomain;
import org.jtransfo.object.SimpleClassDomain;
import org.jtransfo.object.SimpleClassNameTo;
import org.jtransfo.object.SimpleClassTypeTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.jtransfo.internal.ToHelper;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for {@link org.jtransfo.internal.ToHelper}.
 *
 * @author Joachim Van der Auwera
 */
public class ToHelperTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Test
	public void testGetDomainClass() throws Exception {
		assertThat(ToHelper.getDomainClass(new SimpleClassNameTo())).isEqualTo(SimpleClassDomain.class);
		assertThat(ToHelper.getDomainClass(new SimpleClassTypeTo())).isEqualTo(SimpleClassDomain.class);
	}

	@Test
	public void testGetNoDomainClass() throws Exception {
		exception.expect(JTransfoException.class);
		exception.expectMessage(" not annotated with DomainClass.");
		assertThat(ToHelper.getDomainClass(new NoDomain())).isNull();
	}
}

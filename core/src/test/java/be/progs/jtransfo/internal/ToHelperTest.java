package be.progs.jtransfo.internal;

import be.progs.jtransfo.JTransfoException;
import be.progs.jtransfo.object.NoDomain;
import be.progs.jtransfo.object.SimpleClassDomain;
import be.progs.jtransfo.object.SimpleClassNameTo;
import be.progs.jtransfo.object.SimpleClassTypeTo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Test for {@link ToHelper}.
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

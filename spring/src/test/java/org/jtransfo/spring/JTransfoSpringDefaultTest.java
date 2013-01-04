package org.jtransfo.spring;

import org.jtransfo.JTransfo;
import org.jtransfo.object.AddressDomain;
import org.jtransfo.object.AddressTo;
import org.jtransfo.object.PersonDomain;
import org.jtransfo.object.PersonTo;
import org.jtransfo.object.SimpleExtendedDomain;
import org.jtransfo.object.SimpleExtendedTo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Using a customized Spring integrated JTransfo.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:org/jtransfo/spring/jTransfoContext.xml"})
public class JTransfoSpringDefaultTest {

    @Autowired
    private JTransfo jTransfo;

    @Test
    public void testToTo() throws Exception {
        SimpleExtendedDomain domain = new SimpleExtendedDomain();
        domain.setA("aaa");
        domain.setB("bb");
        domain.setC("cccc");
        domain.setI(111);

        SimpleExtendedTo res = new SimpleExtendedTo();
        jTransfo.convert(domain, res);

        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getString()).isEqualTo("bb");
        assertThat(res.getC()).isEqualTo("cccc");
        assertThat(res.getI()).isEqualTo(111);
        assertThat(res.getJ()).isEqualTo(0);
        assertThat(res.getK()).isEqualTo(0);
    }

    @Test
    public void testToDomain() throws Exception {
        SimpleExtendedTo to = new SimpleExtendedTo();
        to.setA("aaa");
        to.setString("bb");
        to.setC("cccc");
        to.setI(111);
        to.setJ(22);
        to.setK(3333);

        SimpleExtendedDomain res = (SimpleExtendedDomain) jTransfo.convert(to);
        assertThat(res.getA()).isEqualTo("aaa");
        assertThat(res.getB()).isEqualTo("bb");
        assertThat(res.getC()).isNull(); // read-only in to
        assertThat(res.getI()).isEqualTo(111);
    }

}

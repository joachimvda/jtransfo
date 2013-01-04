package org.jtransfo.spring;

import org.jtransfo.JTransfoImpl;
import org.jtransfo.ObjectFinder;
import org.jtransfo.TypeConverter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Spring implementation of {@link org.jtransfo.JTransfo}.
 */
public class JTransfoSpring extends JTransfoImpl {

    @Autowired(required = false)
    private List<ObjectFinder> objectFinders;

    @Autowired(required = false)
    private List<TypeConverter> typeConverters;

    @PostConstruct
    protected void postConstruct() {
        if (null != typeConverters) {
            getTypeConverters().addAll(typeConverters);
            updateTypeConverters();
        }

        if (null != objectFinders) {
            getObjectFinders().addAll(objectFinders);
            updateObjectFinders();
        }

    }
}

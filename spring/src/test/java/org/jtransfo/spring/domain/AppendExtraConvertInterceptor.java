package org.jtransfo.spring.domain;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Append value in "extra" field in PersonDomain. Used to convert interceptors and order on convert interceptors.
 */
@Component
@Order(1) // should be at lower order than SetExtraConvertInterceptor to assure it being applied "after"
public class AppendExtraConvertInterceptor implements ConvertInterceptor {

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
        T res = next.convert(source, target, isTargetTo, tags);
        if (target instanceof PersonDomain) {
            PersonDomain person = (PersonDomain) res;
            person.setExtra(person.getExtra() + " sleep.");
        }
        return res;
    }

}

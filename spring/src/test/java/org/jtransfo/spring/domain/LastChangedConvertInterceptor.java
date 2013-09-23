package org.jtransfo.spring.domain;

import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Automatically set the last changed date on the domain object when converting..
 */
@Component
public class LastChangedConvertInterceptor implements ConvertInterceptor {

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
        T res = next.convert(source, target, isTargetTo, tags);
        if (target instanceof PersonDomain) {
            PersonDomain person = (PersonDomain) res;
            person.setLastChanged(new Date());
        }
        return res;
    }

}

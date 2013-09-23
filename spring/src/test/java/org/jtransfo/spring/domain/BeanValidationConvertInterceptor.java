package org.jtransfo.spring.domain;

import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.hibernate.validator.internal.engine.PathImpl;
import org.jtransfo.ConvertInterceptor;
import org.jtransfo.ConvertSourceTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Apply bean validation on the domain objects when converting.
 */
@Component
public class BeanValidationConvertInterceptor implements ConvertInterceptor {

    @Autowired
    private SpringValidatorAdapter validator;

    @Override
    public <T> T convert(Object source, T target, boolean isTargetTo, ConvertSourceTarget next, String... tags) {
        T res = next.convert(source, target, isTargetTo, tags);
        if (!isTargetTo) { // only validate on the domain objects, not on transfer objects
            BindingResult bindingResult = new MapBindingResult(new HashMap(), "");
            validator.validate(target, bindingResult);
            if (bindingResult.hasErrors()) {
                Set<ConstraintViolation<?>> violations = new HashSet<ConstraintViolation<?>>();
                for (ObjectError error : bindingResult.getAllErrors()) {
                    violations.add(new ConstraintViolationImpl(error.getDefaultMessage(), error.getDefaultMessage(),
                            target.getClass(), target, target, target,
                            PathImpl.createPathFromString(error.getObjectName()),
                            null, null));
                }
                throw new MyConstraintViolationException(violations);
            }

        }
        return res;
    }

    private static class MyConstraintViolationException extends ConstraintViolationException {

        private MyConstraintViolationException(Set<ConstraintViolation<?>> constraintViolations) {
            super(constraintViolations);
        }

        @Override
        public String getMessage() {
            return toString();
        }

        @Override
        public String toString() {
            return "ConstraintViolationException " + this.getConstraintViolations();
        }
    }

}

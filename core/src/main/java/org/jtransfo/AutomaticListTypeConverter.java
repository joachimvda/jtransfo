package org.jtransfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.jtransfo.internal.SyntheticField;

public class AutomaticListTypeConverter implements TypeConverter<List<?>, List<?>> {

    private final JTransfo jTransfo;

    public AutomaticListTypeConverter(JTransfoImpl jTransfo) {
        this.jTransfo = jTransfo;
    }

    @Override
    public boolean canConvert(Type realToType, Type realDomainType) {
        Class<?> toClass = getClass(realToType);
        Class<?> domainClass = getClass(realDomainType);
        if (!List.class.isAssignableFrom(toClass) || !List.class.isAssignableFrom(domainClass)) {
            return false;
        }
        Class<?> paramRealToType = getParamType(realToType);
        Class<?> paramRealDomainType = getParamType(realDomainType);
        if (paramRealToType == null || paramRealDomainType == null) {
            return false;
        }
        // TO type should be marked with @DomainClass and domain should match declared
        return jTransfo.isToClass(paramRealToType) && paramRealDomainType.isAssignableFrom(jTransfo.getDomainClass(paramRealToType));
    }

    @Override
    public List<?> convert(List<?> toObjects, SyntheticField domainField, Object domainObject, String... tags) throws JTransfoException {
        if (toObjects == null) {
            return null;
        }
        List<Object> result = new ArrayList<Object>();
        for (Object o : toObjects) {
            result.add(jTransfo.convertTo(o, jTransfo.getDomainClass(o.getClass()), tags));
        }
        return result;
    }

    @Override
    public List<?> reverse(List<?> domainObjects, SyntheticField toField, Object toObject, String... tags) throws JTransfoException {
        if (domainObjects == null) {
            return null;
        }
        List<Object> result = new ArrayList<Object>();
        Class<?> clazz = getParamType(toField.getGenericType());
        for (Object o : domainObjects) {
            result.add(jTransfo.convertTo(o, jTransfo.getToSubType(clazz, o), tags));
        }
        return result;
    }

    private Class<?> getClass(Type type) {
        return  (type instanceof Class ? (Class<?>) type : (Class<?>) ((ParameterizedType) type).getRawType());
    }

    private Class<?> getParamType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType p = (ParameterizedType) type;
            return (Class<?>) p.getActualTypeArguments()[0];
        }
        return null;
    }
}

package org.jtransfo.object;

import org.jtransfo.DomainClass;

/**
 * Transfer object which shows/verifies possible combinations of fields and accessor methods.
 */
@DomainClass(domainClass = MixedMethodAndFieldDomain.class)
public class MixedMethodAndFieldTo {

    private String field;
    private String fieldWithPublicAccessors;
    private String fieldWithPrivateAccessors;
    private String fieldWithPublicGetter;
    private String fieldWithPrivateGetter;

    public MixedMethodAndFieldTo() {
    }

    public MixedMethodAndFieldTo(String field, String fieldWithPublicAccessors, String fieldWithPrivateAccessors, String fieldWithPublicGetter, String fieldWithPrivateGetter) {
        this.field = field;
        this.fieldWithPublicAccessors = fieldWithPublicAccessors;
        this.fieldWithPrivateAccessors = fieldWithPrivateAccessors;
        this.fieldWithPublicGetter = fieldWithPublicGetter;
        this.fieldWithPrivateGetter = fieldWithPrivateGetter;
    }

    public String getFieldAlternate() {
        return field;
    }

    public String getFieldWithPublicAccessorsAlternate() {
        return fieldWithPublicAccessors;
    }

    public String getFieldWithPrivateAccessorsAlternate() {
        return fieldWithPrivateAccessors;
    }

    public String getFieldWithPublicGetterAlternate() {
        return fieldWithPublicGetter;
    }

    public String getFieldWithPrivateGetterAlternate() {
        return fieldWithPrivateGetter;
    }
}

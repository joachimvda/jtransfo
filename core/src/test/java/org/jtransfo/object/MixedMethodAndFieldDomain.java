package org.jtransfo.object;

/**
 * Domain object which shows/verifies possible combinations of fields and accessor methods.
 */
public class MixedMethodAndFieldDomain implements ExtraGetterOnFieldAlternative {

    private String field;
    private String fieldWithPublicAccessors;
    private String fieldWithPrivateAccessors;
    private String fieldWithPublicGetter;
    private String fieldWithPrivateGetter;

    public MixedMethodAndFieldDomain() {
    }

    public MixedMethodAndFieldDomain(String field, String fieldWithPublicAccessors, String fieldWithPrivateAccessors, String fieldWithPublicGetter,
            String fieldWithPrivateGetter) {
        this.field = field;
        this.fieldWithPublicAccessors = fieldWithPublicAccessors;
        this.fieldWithPrivateAccessors = fieldWithPrivateAccessors;
        this.fieldWithPublicGetter = fieldWithPublicGetter;
        this.fieldWithPrivateGetter = fieldWithPrivateGetter;
    }

    public String getFieldWithPublicAccessors() {
        return fieldWithPublicAccessors;
    }

    public void setFieldWithPublicAccessors(String fieldWithPublicAccessors) {
        this.fieldWithPublicAccessors = fieldWithPublicAccessors.substring(0, 3);
    }

    private String getFieldWithPrivateAccessors() {
        return fieldWithPrivateAccessors.substring(0, 3);
    }

    private void setFieldWithPrivateAccessors(String fieldWithPrivateAccessors) {
        this.fieldWithPrivateAccessors = fieldWithPrivateAccessors.substring(0, 3);
    }

    public String getFieldWithPublicGetter() {
        return fieldWithPublicGetter;
    }

    private String getFieldWithPrivateGetter() {
        return fieldWithPrivateGetter.substring(0, 3);
    }

    public String getFieldAlternate() {
        return field;
    }

    public String getFieldWithPrivateAccessorsAlternate() {
        return fieldWithPrivateAccessors;
    }

    public String getFieldWithPrivateGetterAlternate() {
        return fieldWithPrivateGetter;
    }

}

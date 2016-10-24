package org.jtransfo;

import org.jtransfo.object.Gender;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TypeUtilTest extends ArrayList<Gender> {

    public boolean boolField = true;
    public List<String> stringListField;
    public List<List<String>> stringListListField;

    @Test
    public void getRawClass() throws Exception {
        assertThat(TypeUtil.getRawClass(getClass().getField("boolField").getGenericType())).isEqualTo(boolean.class);
        assertThat(TypeUtil.getRawClass(getClass().getField("stringListListField").getGenericType())).isEqualTo(List.class);
        assertThat(TypeUtil.getRawClass(getClass().getField("stringListListField").getGenericType())).isEqualTo(List.class);
        assertThat(TypeUtil.getRawClass(getClass().getMethod("get", int.class).getGenericReturnType())).isEqualTo(Object.class); // cannot determine without
    }

    @Test
    public void getFirstTypeArgument() throws Exception {
        assertThat(TypeUtil.getFirstTypeArgument(getClass().getField("boolField").getGenericType())).isNull();
        assertThat(TypeUtil.getFirstTypeArgument(getClass().getField("stringListField").getGenericType())).isEqualTo(String.class);
        assertThat(TypeUtil.getFirstTypeArgument(getClass().getField("stringListListField").getGenericType())).isEqualTo(List.class);
    }

}
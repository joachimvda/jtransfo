package org.jtransfo.internal;

import org.jtransfo.PreConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jtransfo.PreConverter.Result.CONTINUE;
import static org.jtransfo.PreConverter.Result.SKIP;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Test voor CombinedPreConverter.
 */
public class CombinedPreConverterTest {

    @Spy
    private ResultPreConverter first = new ResultPreConverter(CONTINUE);

    @Spy
    private ResultPreConverter second = new ResultPreConverter(SKIP);

    @Spy
    private ResultPreConverter third = new ResultPreConverter(CONTINUE);

    private List<PreConverter<String, Integer>> preConverters;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        preConverters = Arrays.asList(first, second, third);
    }

    @Test
    public void preConvertToTo() throws Exception {
        CombinedPreConverter<String, Integer> combined = new CombinedPreConverter(preConverters);

        assertThat(combined.preConvertToTo(3, "zzz", "bla")).isEqualTo(SKIP);

        verify(first).preConvertToTo(3, "zzz", "bla");
        verify(second).preConvertToTo(3, "zzz", "bla");
        verify(third, never()).preConvertToTo(3, "zzz", "bla");
    }

    @Test
    public void preConvertToDomain() throws Exception {
        CombinedPreConverter<String, Integer> combined = new CombinedPreConverter(preConverters);

        assertThat(combined.preConvertToDomain("zzz", 3, "bla")).isEqualTo(SKIP);

        verify(first).preConvertToDomain("zzz", 3, "bla");
        verify(second).preConvertToDomain("zzz", 3, "bla");
        verify(third, never()).preConvertToDomain("zzz", 3, "bla");
    }

    private class ResultPreConverter implements PreConverter<String, Integer> {
        private final Result result;

        public ResultPreConverter(Result result) {
            this.result = result;
        }

        @Override
        public Result preConvertToTo(Integer source, String target, String... tags) {
            return result;
        }

        @Override
        public Result preConvertToDomain(String source, Integer target, String... tags) {
            return result;
        }
    }

}
package com.zenika.zenikia.shared.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiCallRetryTest {

    @Test
    void withOneRetry_returnsResultWithoutRetryingWhenFirstCallSucceeds() {
        AtomicInteger calls = new AtomicInteger();

        String result = AiCallRetry.withOneRetry(() -> {
            calls.incrementAndGet();
            return "ok";
        });

        assertThat(result).isEqualTo("ok");
        assertThat(calls).hasValue(1);
    }

    @Test
    void withOneRetry_retriesExactlyOnceAfterAFailureAndSucceeds() {
        AtomicInteger calls = new AtomicInteger();

        String result = AiCallRetry.withOneRetry(() -> {
            if (calls.incrementAndGet() == 1) {
                throw new IllegalStateException("truncated JSON");
            }
            return "ok on retry";
        });

        assertThat(result).isEqualTo("ok on retry");
        assertThat(calls).hasValue(2);
    }

    @Test
    void withOneRetry_givesUpAfterTheSecondFailure() {
        AtomicInteger calls = new AtomicInteger();

        assertThatThrownBy(() -> AiCallRetry.withOneRetry(() -> {
            calls.incrementAndGet();
            throw new IllegalStateException("still broken");
        })).isInstanceOf(IllegalStateException.class).hasMessage("still broken");

        assertThat(calls).hasValue(2);
    }
}

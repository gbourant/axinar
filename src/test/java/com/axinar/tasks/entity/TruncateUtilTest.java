package com.axinar.tasks.entity;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TruncateUtilTest {

    @Test
    public void testTruncateMethod() {
        // We call .parse on both parameters in each assertion to ensure a fresh Instant instance is used every time.

        // An Instant with more than microsecond precision
        assertEquals(Instant.parse("2025-09-24T12:34:56.123456789Z").truncatedTo(ChronoUnit.MICROS), TruncateUtil.truncate(Instant.parse("2025-09-24T12:34:56.123456789Z")), "The Instant should be truncated to microseconds precision");

        // An Instant already truncated to microseconds
        assertEquals(Instant.parse("2025-09-24T12:34:56.123456Z"), TruncateUtil.truncate(Instant.parse("2025-09-24T12:34:56.123456Z")), "The Instant is already truncated to microseconds precision");

        // An Instant without any fractional seconds
        assertEquals(Instant.parse("2025-09-24T12:34:56Z"), TruncateUtil.truncate(Instant.parse("2025-09-24T12:34:56Z")), "The Instant should remain the same as there are no fractional seconds");
    }

}
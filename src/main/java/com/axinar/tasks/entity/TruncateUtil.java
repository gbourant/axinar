package com.axinar.tasks.entity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TruncateUtil {

    private static final ChronoUnit CHRONO_UNIT = ChronoUnit.MICROS;

    public static Instant truncate(Instant instant) {
        return instant.truncatedTo(CHRONO_UNIT);
    }

}

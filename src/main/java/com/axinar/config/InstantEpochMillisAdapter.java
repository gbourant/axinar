package com.axinar.config;

import jakarta.json.bind.adapter.JsonbAdapter;

import java.time.Instant;

public class InstantEpochMillisAdapter implements JsonbAdapter<Instant, Long> {

    @Override
    public Long adaptToJson(Instant instant) {
        return instant == null ? null : instant.toEpochMilli();
    }

    @Override
    public Instant adaptFromJson(Long millis) {
        return millis == null ? null : Instant.ofEpochMilli(millis);
    }
}


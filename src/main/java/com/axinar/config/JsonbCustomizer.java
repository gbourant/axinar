package com.axinar.config;

import io.quarkus.jsonb.JsonbConfigCustomizer;
import jakarta.inject.Singleton;
import jakarta.json.bind.JsonbConfig;

@Singleton
public class JsonbCustomizer implements JsonbConfigCustomizer {

    public void customize(JsonbConfig config) {
        config.withAdapters(new InstantEpochMillisAdapter());
    }
}

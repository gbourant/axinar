package com.axinar.config;

import io.quarkus.smallrye.openapi.OpenApiFilter;
import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.media.Schema;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@OpenApiFilter(OpenApiFilter.RunStage.BOTH)
public class InstantOasFilter implements OASFilter {

    @Override
    public void filterOpenAPI(OpenAPI openAPI) {
        Map<String, Schema> mutableSchemas = new HashMap<>(openAPI.getComponents().getSchemas());
        mutableSchemas.remove("Instant");
        openAPI.getComponents().setSchemas(mutableSchemas);
        openAPI.getComponents().getSchemas().values().forEach(this::updateInstantFields);
    }

    private void updateInstantFields(Schema schema) {

        if (schema.getProperties() != null) {
            schema.getProperties().forEach((name, property) -> {
                if ("#/components/schemas/Instant".equals(property.getRef()) || "date-time".equals(property.getFormat())) {
                    property.setType(Collections.singletonList(Schema.SchemaType.INTEGER));
                    property.setFormat("int64 | timestamp | UTC | Unix Epoch");
                    property.setRef(null);
                }
                updateInstantFields(property);
            });
        }
    }

}

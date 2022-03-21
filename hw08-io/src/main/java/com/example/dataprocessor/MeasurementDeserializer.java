package com.example.dataprocessor;

import com.example.model.Measurement;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.Optional;

public class MeasurementDeserializer extends JsonDeserializer<Measurement> {
    private static final String FIELD_NOT_FOUND = "Not found \"%s\" field in json node \"%s\"";

    @Override
    public Measurement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        String name =
                Optional.ofNullable(node.get("name"))
                        .orElseThrow(() -> new FileProcessException(String.format(FIELD_NOT_FOUND, "name", node)))
                        .asText();

        double value =
                Optional.ofNullable(node.get("value"))
                        .orElseThrow(() -> new FileProcessException(String.format(FIELD_NOT_FOUND, "value", node)))
                        .asDouble();

        return new Measurement(name, value);
    }
}

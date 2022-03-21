package com.example.dataprocessor;

import com.example.model.Measurement;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class ResourcesFileLoader implements Loader {
    private static final String FILE_NOT_FOUND_MESSAGE_TEMPLATE = "Not found in resources file with name %s";

    private final String fileName;
    private final ObjectMapper mapper;

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;

        this.mapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(Measurement.class, new MeasurementDeserializer());
        mapper.registerModule(simpleModule);
    }

    @Override
    public List<Measurement> load() {
        try (var inputStream = loadFileFromResource()) {
            return mapper.readValue(inputStream, new TypeReference<>() {});
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }

    private InputStream loadFileFromResource() {
        InputStream resourceInputStream =
                Optional.ofNullable(getClass().getClassLoader().getResourceAsStream(this.fileName))
                    .orElseThrow(() -> new FileProcessException(String.format(FILE_NOT_FOUND_MESSAGE_TEMPLATE, fileName)));
        return new BufferedInputStream(resourceInputStream);
    }
}

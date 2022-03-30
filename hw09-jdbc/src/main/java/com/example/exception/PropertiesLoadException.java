package com.example.exception;

public class PropertiesLoadException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Not found properties file with name %s";

    public PropertiesLoadException(String propertiesFileName) {
        super(String.format(MESSAGE_TEMPLATE, propertiesFileName));
    }
}

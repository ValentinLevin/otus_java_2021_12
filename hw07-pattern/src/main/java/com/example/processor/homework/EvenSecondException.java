package com.example.processor.homework;

public class EvenSecondException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Exception thrown on %d second";
    private static final long serialVersionUID = 4530327095294723529L;

    public EvenSecondException(int secondOfMinute) {
        super(String.format(MESSAGE_TEMPLATE, secondOfMinute));
    }
}

package com.example.processor.homework;

import com.example.model.Message;
import com.example.processor.Processor;

import java.time.LocalDateTime;

public class ProcessorThrowsExceptionOnEvenSeconds implements Processor {
    private final CurrentSecondOnMinuteProvider currentSecondOfMinuteProvider;

    public ProcessorThrowsExceptionOnEvenSeconds(CurrentSecondOnMinuteProvider currentSecondOfMinuteProvider) {
        this.currentSecondOfMinuteProvider = currentSecondOfMinuteProvider;
    }

    public ProcessorThrowsExceptionOnEvenSeconds() {
        this(() -> LocalDateTime.now().getSecond());
    }

    @Override
    public Message process(Message message) {
        int currentSecondOfMinute = this.currentSecondOfMinuteProvider.getCurrentSecondOfMinute();
        if (currentSecondOfMinute % 2 == 0) {
            throw new EvenSecondException(currentSecondOfMinute);
        }
        return message;
    }
}

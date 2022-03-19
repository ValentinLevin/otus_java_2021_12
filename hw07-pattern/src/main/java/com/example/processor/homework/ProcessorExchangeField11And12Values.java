package com.example.processor.homework;

import com.example.model.Message;
import com.example.processor.Processor;

public class ProcessorExchangeField11And12Values implements Processor {
    @Override
    public Message process(Message message) {
        if (message != null) {
            return message.toBuilder()
                    .field11(message.getField12())
                    .field12(message.getField11())
                    .build();
        }
        return null;
    }
}

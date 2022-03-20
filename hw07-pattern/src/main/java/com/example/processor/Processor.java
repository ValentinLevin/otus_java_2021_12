package com.example.processor;

import com.example.model.Message;

public interface Processor {
    Message process(Message message);
}

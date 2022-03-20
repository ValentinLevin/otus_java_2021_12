package com.example;

import com.example.handler.ComplexProcessor;
import com.example.listener.ListenerPrinterConsole;
import com.example.model.Message;
import com.example.processor.LoggerProcessor;
import com.example.processor.ProcessorConcatFields;
import com.example.processor.ProcessorUpperField10;
import com.example.processor.homework.ProcessorThrowsExceptionOnEvenSeconds;

import java.util.List;

public class Demo {
    public static void main(String[] args) {
        var processors = List.of(new ProcessorConcatFields(),
                new LoggerProcessor(new ProcessorUpperField10()));

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var listenerPrinter = new ListenerPrinterConsole();
        complexProcessor.addListener(listenerPrinter);

        var message = new Message.Builder(1L)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .build();

        var result = complexProcessor.handle(message);
        System.out.println("result:" + result);

        complexProcessor.removeListener(listenerPrinter);
    }
}

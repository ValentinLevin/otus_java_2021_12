package com.example;

import com.example.handler.ComplexProcessor;
import com.example.listener.ListenerPrinterConsole;
import com.example.listener.homework.HistoryListener;
import com.example.model.Message;
import com.example.model.ObjectForMessage;
import com.example.processor.LoggerProcessor;
import com.example.processor.homework.ProcessorExchangeField11And12Values;
import com.example.processor.homework.ProcessorThrowsExceptionOnEvenSeconds;

import java.util.*;
import java.util.stream.IntStream;

public class HomeWork {

    /*
     Реализовать to do:
       1. Добавить поля field11 - field13 (для field13 используйте класс ObjectForMessage)
       2. Сделать процессор, который поменяет местами значения field11 и field12
       3. Сделать процессор, который будет выбрасывать исключение в четную секунду (сделайте тест с гарантированным результатом)
             Секунда должна определяьться во время выполнения.
             Тест - важная часть задания
             Обязательно посмотрите пример к паттерну Мементо!
       4. Сделать Listener для ведения истории (подумайте, как сделать, чтобы сообщения не портились)
          Уже есть заготовка - класс HistoryListener, надо сделать его реализацию
          Для него уже есть тест, убедитесь, что тест проходит
     */

    public static void main(String[] args) {
        var processors =
                List.of(
                        new LoggerProcessor(new ProcessorExchangeField11And12Values()),
                        new ProcessorThrowsExceptionOnEvenSeconds()
                );

        var complexProcessor = new ComplexProcessor(processors, ex -> {});
        var historyListener = new HistoryListener();
        var listenerPrinter = new ListenerPrinterConsole();

        complexProcessor.addListener(listenerPrinter);
        complexProcessor.addListener(historyListener);

        List<Message> messages = IntStream.range(1, 4).mapToObj(HomeWork::generateMessage).toList();

        messages.forEach(complexProcessor::handle);

        complexProcessor.removeListener(historyListener);
        complexProcessor.removeListener(listenerPrinter);
    }

    private static Message generateMessage(long messageId) {
        var objectForMessage = new ObjectForMessage();
        objectForMessage.setData(IntStream.range(0, 10).mapToObj(String::valueOf).toList());

        return new Message.Builder(messageId)
                .field1("field1")
                .field2("field2")
                .field3("field3")
                .field6("field6")
                .field10("field10")
                .field11("field11")
                .field12("field12")
                .field13(objectForMessage)
                .build();
    }
}

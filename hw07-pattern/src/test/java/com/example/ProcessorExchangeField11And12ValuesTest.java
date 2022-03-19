package com.example;

import com.example.model.Message;
import com.example.processor.homework.ProcessorExchangeField11And12Values;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка команды на обмен значениями между полями field11 и field12")
public class ProcessorExchangeField11And12ValuesTest {

    @Test
    @DisplayName("Что исходное сообщение осталось без изменений, а значения полей корректно изменены")
    public void exchangeValuesTest() {
        Message message =
                new Message.Builder(1)
                        .field11("field11")
                        .field12("field12")
                        .build();

        var processor = new ProcessorExchangeField11And12Values();
        Message changedMessage = processor.process(message);

        Assertions.assertThat(changedMessage.getField11()).isEqualTo(message.getField12());
        Assertions.assertThat(changedMessage.getField12()).isEqualTo(message.getField11());
    }
}

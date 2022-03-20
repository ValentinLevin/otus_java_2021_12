package com.example;

import com.example.processor.homework.EvenSecondException;
import com.example.processor.homework.ProcessorThrowsExceptionOnEvenSeconds;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Проверка команды на проброс исключения если она вызвана в четную секунду")
public class ProcessorThrowsExceptionOnEvenSecondsTest {

    @Test
    @DisplayName("Пробросит исключение на четной секунде")
    public void throwsExceptionOnEvenSecondTest() {
        var processor = new ProcessorThrowsExceptionOnEvenSeconds(() -> 2);
        Assertions.assertThatThrownBy(() -> {
            processor.process(null);
        }).isInstanceOf(EvenSecondException.class);
    }
    @Test
    @DisplayName("Не пробросит исключение на нечетной секунде")
    public void notThrowsExceptionOnOddSecondTest() {
        var processor = new ProcessorThrowsExceptionOnEvenSeconds(() -> 1);
        Assertions.assertThatCode(() -> {
            processor.process(null);
        }).doesNotThrowAnyException();
    }
}

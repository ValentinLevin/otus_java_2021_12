package com.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.ZoneId;

class MyDateTest {
    private MyDate myDate;

    @BeforeEach()
    public void beforeEach() {
        this.myDate = new MyDate(Instant.ofEpochMilli(1643616249000L).atZone(ZoneId.of("Asia/Almaty")).toLocalDateTime());
    }

    @Test
    @DisplayName("Проверка корректности форматирования даты")
    public void testDateFormatting() {
        String expectedValue = "31.01.2022";
        String actualValue = this.myDate.getFormattedDate();
        Assertions.assertThat(expectedValue).isEqualTo(actualValue);
    }

    @Test
    public void testTimeFormatting() {
        String expectedValue = "14:04:09";
        String actualValue = this.myDate.getFormattedTime();
        Assertions.assertThat(expectedValue).isEqualTo(actualValue);
    }
}
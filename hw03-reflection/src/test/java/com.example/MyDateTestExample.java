package com.example;

import com.example.framework.annotations.After;
import com.example.framework.annotations.Before;
import com.example.framework.annotations.Test;
import com.example.framework.assertions.Assertions;

import java.time.Instant;
import java.time.ZoneId;

public class MyDateTestExample {
    private MyDate myDate;

    @Before
    public void beforeEach() {
        this.myDate = new MyDate(Instant.ofEpochMilli(1643616249000L).atZone(ZoneId.of("Asia/Almaty")).toLocalDateTime());
    }

    @Test
    public void passedTestDateFormatting() {
        String expectedValue = "31.01.2022";
        String actualValue = this.myDate.getFormattedDate();
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    public void failedTestDateFormatting() {
        String expectedValue = "31.01.2021";
        String actualValue = this.myDate.getFormattedDate();
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    public void passedTestTimeFormatting() {
        String expectedValue = "14:04:09";
        String actualValue = this.myDate.getFormattedTime();
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test
    public void failedTestTimeFormatting() {
        String expectedValue = "09:04:09";
        String actualValue = this.myDate.getFormattedTime();
        Assertions.assertEquals(expectedValue, actualValue);
    }

    @Test(disabled = true)
    public void skippedTest() {
        throw new RuntimeException();
    }

    @After
    public void AfterEach() {
        System.out.println("afterEach method invocation");
    }
}

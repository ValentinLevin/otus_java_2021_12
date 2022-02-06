package com.example.calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SummatorTest {
    private Summator summator;

    @BeforeEach
    void setup() {
        this.summator = new Summator();
        for (int i = 0; i < 1000; i++) {
            summator.calc(new Data(i));
        }
    }

    @Test
    public void summatorSumTest() {
        int expectedValue = 499500;
        int actualValue = summator.getSum();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void summatorPrevValueTest() {
        int expectedValue = 999;
        int actualValue = summator.getPrevValue();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void summatorPrevPrevValueTest() {
        int expectedValue = 998;
        int actualValue = summator.getPrevPrevValue();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void summatorSumLastThreeValueTest() {
        int expectedValue = 2994;
        int actualValue = summator.getSumLastThreeValues();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    void summatorSomeValueTest() {
        int expectedValue = 245993;
        int actualValue = summator.getSomeValue();
        Assertions.assertThat(actualValue).isEqualTo(expectedValue);
    }
}

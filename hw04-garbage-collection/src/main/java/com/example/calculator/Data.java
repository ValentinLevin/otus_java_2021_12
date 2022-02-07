package com.example.calculator;

public class Data {
    private int value;

    public Data(int value) {
        this.value = value;
    }

    public Data() {}

    public int getValue() {
        return value;
    }

    public Data setValue(int value) {
        this.value = value;
        return this;
    }
}

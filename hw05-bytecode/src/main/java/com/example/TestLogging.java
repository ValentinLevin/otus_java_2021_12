package com.example;

import com.example.annotations.Log;

public class TestLogging {
    @Log
    public static void staticsqrt(int x) {
        System.out.printf("static sqrt of %d is %d%n", x, x * x);
    }

    @Log
    public void sqrt(int x) {
        System.out.printf("sqrt of %d is %d%n", x, x * x);
    }

    @Log
    public void sqrt(int x, String value) {
        System.out.printf("sqrt of %d is %d (%s)%n", x, x * x, value);
    }

//    @Log
    public void sum(int x, int y) {
        System.out.printf("sum of %d and %d is %d%n", x, y, x + y);
    }

    @Log
    public void sub(int x, int y) {
        System.out.printf("sub of %d and %d is %d%n", x, y, x - y);
    }

    @Log
    public void sum(int x, int y, int z) {
        System.out.printf("sum of %d, %d and %d is %d%n", x, y, z, x + y + z);
    }
}

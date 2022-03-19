package com.example.homework;


import java.util.Deque;
import java.util.LinkedList;

public class CustomerReverseOrder {
    private final Deque<Customer> stack = new LinkedList<>();

    public void add(Customer customer) {
        this.stack.push(customer);
    }

    public Customer take() {
        return this.stack.pop();
    }
}

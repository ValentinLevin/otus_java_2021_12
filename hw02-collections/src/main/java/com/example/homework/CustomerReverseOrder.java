package com.example.homework;


import java.util.Deque;
import java.util.LinkedList;

public class CustomerReverseOrder {
    private final Deque<Customer> stack = new LinkedList<>();

    //todo: 2. надо реализовать методы этого класса
    //надо подобрать подходящую структуру данных, тогда решение будет в "две строчки"

    public void add(Customer customer) {
        this.stack.push(customer);
    }

    public Customer take() {
        return this.stack.pop();
    }
}
